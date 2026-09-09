package com.mudassar.notes.backend.data.notes

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.NotesSaveResult
import com.mudassar.notes.backend.domain.model.NotesSaveResult.NotesConflict
import com.mudassar.notes.backend.domain.model.NotesSaveResult.Success
import com.mudassar.notes.backend.domain.model.UserId
import com.mudassar.notes.backend.domain.repository.NotesRepository
import com.mudassar.notes.backend.util.StringsProvider.getString
import org.springframework.stereotype.Repository

@Repository
class NotesRepositoryImpl(
    private val notesStore: NotesStore,
) : NotesRepository {
    override fun save(note: NoteSync): NotesSaveResult =
        notesStore.update(note.userId.value, note.id) { existing ->
            val currentVersion = existing?.version ?: 0
            if (note.version != currentVersion) {
                NotesStore.Update(
                    newValue = existing,
                    result = NotesConflict(
                        serverVersion = currentVersion,
                        reason = versionConflictMessage()
                    ),
                )
            } else {
                val newVersion = currentVersion + 1
                NotesStore.Update(
                    newValue = note.copy(version = newVersion),
                    result = Success(version = newVersion),
                )
            }
        }

    override fun delete(note: NoteSync): NotesConflict? =
        notesStore.update(note.userId.value, note.id) { existing ->
            if (existing != null && existing.version != note.version) {
                NotesStore.Update(
                    newValue = existing,
                    result = NotesConflict(
                        serverVersion = existing.version,
                        reason = versionConflictMessage()
                    ),
                )
            } else {
                NotesStore.Update(newValue = null, result = null)
            }
        }

    override fun getAll(userId: UserId): List<NoteSync> =
        notesStore.getAll(userId.value)

    override fun getById(userId: UserId, id: String): NoteSync? =
        notesStore.get(userId.value, id)

    private fun versionConflictMessage(): String {
        return getString("sync.error.version-mismatch")
    }
}
