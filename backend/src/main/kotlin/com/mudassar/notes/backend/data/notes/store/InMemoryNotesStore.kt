package com.mudassar.notes.backend.data

import com.mudassar.notes.backend.data.notes.NotesStore
import com.mudassar.notes.backend.domain.model.NoteSync
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryNotesStore : NotesStore {
    private val notes = ConcurrentHashMap<String, NoteSync>()

    override fun put(note: NoteSync) {
        notes[note.id] = note
    }

    override fun remove(id: String) {
        notes.remove(id)
    }
}
