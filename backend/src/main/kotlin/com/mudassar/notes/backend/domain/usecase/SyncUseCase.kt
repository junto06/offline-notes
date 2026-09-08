package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.SyncOperation
import com.mudassar.notes.backend.domain.model.SyncResult
import com.mudassar.notes.backend.domain.repository.NotesRepository
import org.springframework.stereotype.Service

@Service
class SyncUseCase(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(notes: List<NoteSync>): SyncResult {
        notes.forEach { note ->
            when (note.operation) {
                SyncOperation.UPDATE -> notesRepository.save(note)
                SyncOperation.DELETE -> notesRepository.delete(note.id)
            }
        }
        return SyncResult()
    }
}
