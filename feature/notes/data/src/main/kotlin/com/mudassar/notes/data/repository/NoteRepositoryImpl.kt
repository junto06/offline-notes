package com.mudassar.notes.data.repository

import com.mudassar.notes.base.DispatcherProvider
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.data.local.NoteDao
import com.mudassar.notes.data.mapper.toDto
import com.mudassar.notes.data.mapper.toEntity
import com.mudassar.notes.data.mapper.toNote
import com.mudassar.notes.data.mapper.toNotes
import com.mudassar.notes.data.remote.ErrorResponseDto
import com.mudassar.notes.data.remote.NotesService
import com.mudassar.notes.data.remote.SyncNotesResponseDto
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.NoteStatus
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
            response.errors.isEmpty()
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
            val failureReason = response.errors[note.id.value]
            when {
                failureReason != null ->
                    toUpdate += note.copy(status = NoteStatus.ERROR, failureReason = failureReason)

                note.deleted -> toDelete += note.id.value

                else -> toUpdate += note.copy(status = NoteStatus.SYNCED, failureReason = null)
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
}
