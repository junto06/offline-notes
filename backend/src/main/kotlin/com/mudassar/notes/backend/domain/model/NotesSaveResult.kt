package com.mudassar.notes.backend.domain.model

sealed interface NotesSaveResult {
    data class Success(val version: Long) : NotesSaveResult
    data class NotesConflict(
        val serverVersion: Long,
        val reason: String
    ) : NotesSaveResult
}
