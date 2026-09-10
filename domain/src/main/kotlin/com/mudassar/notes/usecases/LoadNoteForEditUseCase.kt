package com.mudassar.notes.usecases

import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoadNoteForEditUseCase @Inject constructor(
    private val newNoteUseCase: NewNoteUseCase,
    private val observeNoteUseCase: ObserveNoteUseCase,
) {
    // null for an existing id means the note was deleted (locally or by a remote refresh) -
    // there's no "still loading" state to confuse it with, Room emits the current row or null.
    operator fun invoke(id: NoteId?): Flow<Note?> =
        if (id == null) flowOf(newNoteUseCase())
        else observeNoteUseCase(id)
}
