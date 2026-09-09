package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.model.NotesSaveResult
import com.mudassar.notes.backend.domain.model.SyncOperation
import com.mudassar.notes.backend.domain.model.SyncResult
import com.mudassar.notes.backend.domain.repository.NotesRepository
import org.springframework.stereotype.Service

@Service
class SyncUseCase(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(notes: List<NoteSync>): SyncResult {
        val conflicts = mutableMapOf<String, NotesSaveResult.NotesConflict>()
        val versions = mutableMapOf<String, Long>()
        notes.forEach { note ->
            when (note.operation) {
                SyncOperation.UPDATE -> when (val result = notesRepository.save(note)) {
                    is NotesSaveResult.Success -> versions[note.id] = result.version
                    is NotesSaveResult.NotesConflict -> conflicts[note.id] = result
                }

                SyncOperation.DELETE -> {
                    notesRepository.delete(note)?.let { conflicts[note.id] = it }
                }
            }
        }
        return SyncResult(
            conflicts = conflicts,
            versions = versions,
        )
    }
}
