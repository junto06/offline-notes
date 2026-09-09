package com.mudassar.notes.backend.domain.model

data class NoteSync(
    val id: String,
    val userId: UserId,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val operation: SyncOperation,
    val version: Long,
)

enum class SyncOperation {
    UPDATE,
    DELETE,
}

data class SyncResult(
    val conflicts: Map<String, NotesSaveResult.NotesConflict> = emptyMap(),
    val versions: Map<String, Long> = emptyMap(),
)
