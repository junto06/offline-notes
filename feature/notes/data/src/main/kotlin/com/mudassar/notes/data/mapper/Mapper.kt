package com.mudassar.notes.data.mapper

import com.mudassar.notes.base.util.byName
import com.mudassar.notes.data.local.NoteEntity
import com.mudassar.notes.data.remote.NoteDto
import com.mudassar.notes.data.remote.NoteOperationDto
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.NoteStatus
import kotlin.time.Instant

fun List<NoteEntity>.toNotes(): List<Note> =
    map(NoteEntity::toNote)

fun NoteEntity.toNote(): Note = Note(
    id = NoteId(id),
    title = title,
    content = content,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
    status = status.byName<NoteStatus>(),
    failureReason = failureReason,
    deleted = deleted,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id.value,
    title = title,
    content = content,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds(),
    status = status.name,
    failureReason = failureReason,
    deleted = deleted,
)

fun Note.toDto(): NoteDto = NoteDto(
    id = id.value,
    title = title,
    content = content,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds(),
    operation = if (deleted) NoteOperationDto.DELETE else NoteOperationDto.UPDATE,
)
