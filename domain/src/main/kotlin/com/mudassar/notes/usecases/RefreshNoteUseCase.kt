package com.mudassar.notes.usecases

import com.mudassar.notes.models.NoteId
import com.mudassar.notes.repository.NoteRepository
import javax.inject.Inject

class RefreshNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(id: NoteId): Boolean =
        repository.refreshNote(id)
}
