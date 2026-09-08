package com.mudassar.notes.presentation.list

import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.NoteStatus

data class NoteUiModel(
    val id: NoteId,
    val title: String,
    val updatedAt: String,
    val status: NoteStatus,
)
