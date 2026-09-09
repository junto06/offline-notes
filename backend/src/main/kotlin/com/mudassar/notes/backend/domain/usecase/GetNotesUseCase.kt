package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.UserId
import com.mudassar.notes.backend.domain.repository.NotesRepository
import org.springframework.stereotype.Service

@Service
class GetNotesUseCase(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(userId: UserId): List<NoteSync> =
        notesRepository.getAll(userId)
}
