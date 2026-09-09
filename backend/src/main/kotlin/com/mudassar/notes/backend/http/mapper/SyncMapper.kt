package com.mudassar.notes.backend.http.mapper

import com.mudassar.notes.backend.domain.model.ConflictResolution
import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.ResolveNotesConflictRequest
import com.mudassar.notes.backend.domain.model.SyncOperation
import com.mudassar.notes.backend.domain.model.SyncResult
import com.mudassar.notes.backend.domain.model.UserId
import com.mudassar.notes.backend.http.dto.ConflictDto
import com.mudassar.notes.backend.http.dto.NoteDto
import com.mudassar.notes.backend.http.dto.NoteResponseDto
import com.mudassar.notes.backend.http.dto.ResolveConflictRequestDto
import com.mudassar.notes.backend.http.dto.SyncNotesResponseDto

fun NoteDto.toDomain(userId: UserId): NoteSync = NoteSync(
    id = id,
    userId = userId,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    operation = SyncOperation.valueOf(operation),
    version = version,
)

fun SyncResult.toDto(): SyncNotesResponseDto =
    SyncNotesResponseDto(
        conflicts = conflicts.mapValues { (_, conflict) ->
            ConflictDto(serverVersion = conflict.serverVersion, reason = conflict.reason)
        },
        versions = versions,
    )

fun NoteSync.toResponseDto(): NoteResponseDto = NoteResponseDto(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    version = version,
)

fun ResolveConflictRequestDto.toDomain(userId: UserId, id: String): ResolveNotesConflictRequest = ResolveNotesConflictRequest(
    userId = userId,
    id = id,
    resolution = ConflictResolution.valueOf(resolution),
    expectedVersion = version,
    title = title.orEmpty(),
    content = content.orEmpty(),
)
