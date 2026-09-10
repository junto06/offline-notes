package com.mudassar.notes.backend.domain.repository

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.NotesSaveResult
import com.mudassar.notes.backend.domain.model.NotesSaveResult.NotesConflict
import com.mudassar.notes.backend.domain.model.UserId

interface NotesRepository {
    // [NoteSync.userId] determines which user's notes this applies to.
    fun save(note: NoteSync): NotesSaveResult

    // Returns the conflict if [NoteSync.version] doesn't match
    // the stored version, or null on success.
    fun delete(note: NoteSync): NotesConflict?

    fun getAll(userId: UserId, since: Long? = null): List<NoteSync>

    fun getById(userId: UserId, id: String): NoteSync?
}
