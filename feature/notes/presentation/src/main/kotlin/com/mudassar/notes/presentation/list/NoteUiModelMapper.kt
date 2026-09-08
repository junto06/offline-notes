package com.mudassar.notes.presentation.list

import com.mudassar.notes.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DateFormat = "MMM d, HH:mm"

object NoteUiModelMapper {

    private fun SimpleDateFormat.map(note: Note): NoteUiModel = NoteUiModel(
        id = note.id,
        title = note.title.ifBlank { "Untitled" },
        updatedAt = format(Date(note.updatedAt.toEpochMilliseconds())),
        status = note.status,
    )

    fun map(notes: List<Note>): List<NoteUiModel> {
        val formatter = SimpleDateFormat(DateFormat, Locale.getDefault())
        return notes.map { formatter.map(it) }
    }
}
