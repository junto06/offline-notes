package com.mudassar.notes.backend.http.dto

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.SyncOperation
import com.mudassar.notes.backend.domain.model.SyncResult

fun NoteDto.toDomain(): NoteSync = NoteSync(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    operation = SyncOperation.valueOf(operation),
)

fun SyncResult.toDto(): SyncNotesResponseDto = SyncNotesResponseDto(errors = errors)
