package com.mudassar.notes.data.repository

import com.mudassar.notes.base.DispatcherProvider
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.data.local.NoteDao
import com.mudassar.notes.data.mapper.toDto
import com.mudassar.notes.data.mapper.toEntity
import com.mudassar.notes.data.mapper.toNote
import com.mudassar.notes.data.mapper.toNotes
import com.mudassar.notes.data.remote.ConflictDto
import com.mudassar.notes.data.remote.ErrorResponseDto
import com.mudassar.notes.data.remote.NoteResponseDto
import com.mudassar.notes.data.remote.NotesService
import com.mudassar.notes.data.remote.ResolveConflictRequestDto
import com.mudassar.notes.data.remote.SyncNotesResponseDto
import com.mudassar.notes.models.ConflictResolution
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.NoteStatus
import com.mudassar.notes.models.ResolveConflictResult
import com.mudassar.notes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val dispatcherProvider: DispatcherProvider,
    private val notesService: NotesService,
    private val json: Json,
    private val errorLogger: ErrorLogger,
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        noteDao.observeNotes()
            .map { it.toNotes() }
            .flowOn(dispatcherProvider.io)

    override fun observeNote(id: NoteId): Flow<Note?> =
        noteDao.observeNote(id.value)
            .map { it?.toNote() }
            .flowOn(dispatcherProvider.io)

    override suspend fun saveNote(note: Note) =
        noteDao.upsertNote(note.toEntity())

    override suspend fun syncNotes(): Boolean {
        val pending = noteDao.getNotesByStatus(NoteStatus.PENDING.name).toNotes()
        if (pending.isEmpty()) return true

        return try {
            val response = notesService.syncNotes(pending.map { it.toDto() })
            updateNotes(pending, response)
            response.conflicts.isEmpty()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorLogger.logError(e, "Failed to sync ${pending.size} pending note(s)")
            val failureReason = e.extractFailureReason()
            markFailure(pending, failureReason)
            false
        }
    }

    private suspend fun updateNotes(
        pending: List<Note>,
        response: SyncNotesResponseDto
    ) {
        val toUpdate = mutableListOf<Note>()
        val toDelete = mutableListOf<String>()

        for (note in pending) {
            val conflict = response.conflicts[note.id.value]
            when {
                conflict != null -> {
                    toUpdate += note.copy(
                        status = NoteStatus.CONFLICT,
                        failureReason = conflict.reason,
                        conflictServerVersion = conflict.serverVersion,
                    )
                }

                note.deleted -> {
                    toDelete += note.id.value
                }

                else -> {
                    toUpdate += note.copy(
                        status = NoteStatus.SYNCED,
                        failureReason = null,
                        conflictServerVersion = null,
                        version = response.versions[note.id.value] ?: note.version,
                    )
                }
            }
        }

        if (toUpdate.isNotEmpty()) {
            noteDao.upsertNotes(toUpdate.map { it.toEntity() })
        }
        if (toDelete.isNotEmpty()) {
            noteDao.deleteNotes(toDelete)
        }
    }

    private suspend fun markFailure(
        pending: List<Note>,
        failureReason: String?
    ) {
        noteDao.upsertNotes(
            pending.map { note ->
                note.copy(
                    status = NoteStatus.ERROR,
                    failureReason = failureReason
                ).toEntity()
            }
        )
    }

    private fun Throwable.extractFailureReason(): String? {
        val errorBody = (this as? HttpException)?.response()?.errorBody() ?: return null
        return runCatching { json.decodeFromString<ErrorResponseDto>(errorBody.string()).message }
            .getOrNull()
    }

    override suspend fun fetchNotes(): Boolean {
        return try {
            storeFetchedNotes(notesService.getAll())
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorLogger.logError(e, "Failed to fetch notes from server")
            false
        }
    }

    private suspend fun storeFetchedNotes(remoteNotes: List<NoteResponseDto>) {
        if (remoteNotes.isEmpty()) return
        val remoteIds = remoteNotes.map { it.id }
        val localIds = noteDao.getNotesByIds(remoteIds).associateBy { it.id }
        // ignore any notes that are PENDING/ERROR/CONFLICT as that will get
        // resolved as a normal version conflict the next time it's pushed.
        val applicable = remoteNotes.filter { note ->
            val localStatus = localIds[note.id]?.status
            localStatus == null || localStatus == NoteStatus.SYNCED.name
        }
        val (toDelete, toUpsert) = applicable.partition { it.deleted }

        if (toDelete.isNotEmpty()) {
            noteDao.deleteNotes(toDelete.map { it.id })
        }
        if (toUpsert.isNotEmpty()) {
            noteDao.upsertNotes(toUpsert.map { it.toNote().toEntity() })
        }
    }

    override suspend fun resolveConflict(note: Note, resolution: ConflictResolution): ResolveConflictResult {
        val expectedVersion = note.conflictServerVersion ?: note.version
        val request = ResolveConflictRequestDto(
            resolution = resolution.name,
            title = note.title,
            content = note.content,
            version = expectedVersion,
        )

        return try {
            val response = notesService.resolveConflict(note.id.value, request)
            when {
                response.isSuccessful -> {
                    val body = response.body() ?: return ResolveConflictResult.Failed
                    val resolved = body.toNote()
                    noteDao.upsertNote(resolved.toEntity())
                    ResolveConflictResult.Resolved(resolved)
                }

                response.code() == 409 -> {
                    val conflict = response.errorBody()?.let {
                        runCatching { json.decodeFromString<ConflictDto>(it.string()) }.getOrNull()
                    } ?: return ResolveConflictResult.Failed
                    noteDao.upsertNote(
                        note.copy(
                            status = NoteStatus.CONFLICT,
                            failureReason = conflict.reason,
                            conflictServerVersion = conflict.serverVersion,
                        ).toEntity()
                    )
                    ResolveConflictResult.StillConflicting(conflict.serverVersion, conflict.reason)
                }

                else -> ResolveConflictResult.Failed
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorLogger.logError(e, "Failed to resolve conflict for note ${note.id.value}")
            ResolveConflictResult.Failed
        }
    }
}
