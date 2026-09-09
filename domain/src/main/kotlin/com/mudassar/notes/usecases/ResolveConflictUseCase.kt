package com.mudassar.notes.usecases

import com.mudassar.notes.models.ConflictResolution
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.ResolveConflictResult
import com.mudassar.notes.repository.NoteRepository
import javax.inject.Inject

class ResolveConflictUseCase @Inject constructor(
    private val repository: NoteRepository,
) {
    suspend operator fun invoke(note: Note, resolution: ConflictResolution): ResolveConflictResult =
        repository.resolveConflict(note, resolution)
}
