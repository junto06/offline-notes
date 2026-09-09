package com.mudassar.notes.usecases

import com.mudassar.notes.repository.NoteRepository
import com.mudassar.notes.repository.SessionRepository
import com.mudassar.notes.repository.isLoggedIn
import javax.inject.Inject

class SyncUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): Boolean {
        // nothing to push against without a logged-in session
        if (!sessionRepository.isLoggedIn()) return true
        return noteRepository.syncNotes()
    }
}
