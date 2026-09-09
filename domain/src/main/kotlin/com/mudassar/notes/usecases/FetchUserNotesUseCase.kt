package com.mudassar.notes.usecases

import com.mudassar.notes.repository.NoteRepository
import com.mudassar.notes.repository.SessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FetchUserNotesUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): Boolean {
        val hasSession = sessionRepository.observeUser().first() != null
        if (!hasSession) return true // nothing to fetch without a logged-in session
        return noteRepository.fetchNotes()
    }
}
