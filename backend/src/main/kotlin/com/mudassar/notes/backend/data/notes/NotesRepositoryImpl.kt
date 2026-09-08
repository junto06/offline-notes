package com.mudassar.notes.backend.data.notes

import com.mudassar.notes.backend.domain.model.NoteSync
import com.mudassar.notes.backend.domain.repository.NotesRepository
import org.springframework.stereotype.Repository

@Repository
class NotesRepositoryImpl(
    private val notesStore: NotesStore,
) : NotesRepository {
    override fun save(note: NoteSync) {
        notesStore.put(note)
    }

    override fun delete(id: String) {
        notesStore.remove(id)
    }
}
