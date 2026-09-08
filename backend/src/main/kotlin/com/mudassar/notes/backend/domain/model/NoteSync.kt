package com.mudassar.notes.backend.domain.model

data class NoteSync(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val operation: SyncOperation,
)

enum class SyncOperation {
    UPDATE,
    DELETE,
}

data class SyncResult(
    val errors: Map<String, String> = emptyMap(),
)
