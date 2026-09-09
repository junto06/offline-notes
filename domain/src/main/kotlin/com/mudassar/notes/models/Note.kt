package com.mudassar.notes.models

import kotlin.time.Instant

data class Note(
    val id: NoteId,
    val title: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: NoteStatus = NoteStatus.PENDING,
    val failureReason: String? = null,
    val deleted: Boolean = false,
    val version: Long,
    val conflictServerVersion: Long? = null,
)

@JvmInline
value class NoteId(val value: String)
