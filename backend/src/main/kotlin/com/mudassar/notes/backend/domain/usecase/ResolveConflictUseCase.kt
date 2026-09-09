package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.exception.NoteNotFoundException
import com.mudassar.notes.backend.domain.model.ConflictResolution
import com.mudassar.notes.backend.domain.model.ResolveNotesConflictOutcome
import com.mudassar.notes.backend.domain.model.ResolveNotesConflictRequest
import com.mudassar.notes.backend.domain.model.NotesSaveResult
import com.mudassar.notes.backend.domain.model.SyncOperation
import com.mudassar.notes.backend.domain.repository.NotesRepository
import com.mudassar.notes.backend.util.ClockProvider
import org.springframework.stereotype.Service

@Service
class ResolveConflictUseCase(
    private val notesRepository: NotesRepository,
    private val clockProvider: ClockProvider,
) {
    operator fun invoke(request: ResolveNotesConflictRequest): ResolveNotesConflictOutcome {
        val existing = notesRepository.getById(request.userId, request.id) ?: throw NoteNotFoundException()

        return when (request.resolution) {
            // Client discards its local edits and adopts whatever is currently on the server.
            ConflictResolution.KEEP_REMOTE -> ResolveNotesConflictOutcome.Resolved(existing)

            // Client's edits win, but only if nothing else changed the note since the client
            // last saw it - reuses the same optimistic-lock check as a normal sync save, so a
            // race against a third write surfaces as a fresh Conflict rather than silently
            // overwriting it.
            ConflictResolution.KEEP_MINE -> {
                val candidate = existing.copy(
                    title = request.title,
                    content = request.content,
                    updatedAt = clockProvider().toEpochMilli(),
                    operation = SyncOperation.UPDATE,
                    version = request.expectedVersion,
                )
                when (val result = notesRepository.save(candidate)) {
                    is NotesSaveResult.Success ->
                        ResolveNotesConflictOutcome.Resolved(candidate.copy(version = result.version))
                    is NotesSaveResult.NotesConflict ->
                        ResolveNotesConflictOutcome.Conflict(result.serverVersion, result.reason)
                }
            }
        }
    }
}
