package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.UserId
import com.mudassar.notes.backend.domain.repository.NotesRepository
import org.springframework.stereotype.Service

@Service
class GetNoteUseCase(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(userId: UserId, id: String): NoteSync? =
        notesRepository.getById(userId, id)
}
