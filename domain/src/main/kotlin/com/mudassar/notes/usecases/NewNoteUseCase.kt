package com.mudassar.notes.usecases

import com.mudassar.notes.base.ClockProvider
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import java.util.UUID
import javax.inject.Inject

class NewNoteUseCase @Inject constructor(
    private val clockProvider: ClockProvider,
) {
    operator fun invoke(): Note {
        val now = clockProvider.now()
        return Note(
            id = NoteId(UUID.randomUUID().toString()),
            title = "",
            content = "",
            createdAt = now,
            updatedAt = now,
        )
    }
}
