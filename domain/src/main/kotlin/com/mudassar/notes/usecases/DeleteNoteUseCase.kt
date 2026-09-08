package com.mudassar.notes.usecases

import com.mudassar.notes.base.ClockProvider
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteStatus
import com.mudassar.notes.repository.NoteRepository
import com.mudassar.notes.sync.ScheduleSync
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
    private val clockProvider: ClockProvider,
    private val scheduleSync: ScheduleSync,
) {
    suspend operator fun invoke(note: Note) {
        repository.saveNote(
            note.copy(
                deleted = true,
                updatedAt = clockProvider.now(),
                status = NoteStatus.PENDING,
                failureReason = null,
            )
        )
        scheduleSync.schedule()
    }
}
