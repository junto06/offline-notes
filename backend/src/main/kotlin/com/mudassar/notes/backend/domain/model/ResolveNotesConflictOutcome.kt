package com.mudassar.notes.backend.domain.model

sealed interface ResolveNotesConflictOutcome {
    data class Resolved(val note: NoteSync) : ResolveNotesConflictOutcome
    data class Conflict(val serverVersion: Long, val reason: String) : ResolveNotesConflictOutcome
}
