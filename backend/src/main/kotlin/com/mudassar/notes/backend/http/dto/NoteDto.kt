package com.mudassar.notes.backend.http.dto

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val operation: String,
    val version: Long = 0, // for compatability
)

data class SyncNotesResponseDto(
    val conflicts: Map<String, ConflictDto> = emptyMap(),
    val versions: Map<String, Long> = emptyMap(),
)

data class ConflictDto(
    val serverVersion: Long,
    val reason: String,
)

data class NoteResponseDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val version: Long,
    val deleted: Boolean,
)

data class ResolveConflictRequestDto(
    val resolution: String,
    val title: String? = null,
    val content: String? = null,
    val version: Long,
)
