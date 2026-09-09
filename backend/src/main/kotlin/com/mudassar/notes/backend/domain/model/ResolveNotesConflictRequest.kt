package com.mudassar.notes.backend.domain.model

data class ResolveNotesConflictRequest(
    val userId: UserId,
    val id: String,
    val resolution: ConflictResolution,
    val expectedVersion: Long,
    val title: String,
    val content: String,
)
