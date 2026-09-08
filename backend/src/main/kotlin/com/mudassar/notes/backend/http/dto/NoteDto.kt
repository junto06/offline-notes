package com.mudassar.notes.backend.http.dto

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val operation: String,
)

data class SyncNotesResponseDto(
    val errors: Map<String, String> = emptyMap(),
)
