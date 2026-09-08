package com.mudassar.notes.usecases

import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    operator fun invoke(id: NoteId): Flow<Note?> = repository.observeNote(id)
}
