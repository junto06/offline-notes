package com.mudassar.notes.usecases

import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoadNoteForEditUseCase @Inject constructor(
    private val newNoteUseCase: NewNoteUseCase,
    private val observeNoteUseCase: ObserveNoteUseCase,
) {
    operator fun invoke(id: NoteId?): Flow<Note> =
        if (id == null) flowOf(newNoteUseCase())
        else observeNoteUseCase(id).filterNotNull()
}
