package com.mudassar.notes.backend.data.notes

import com.mudassar.notes.backend.domain.model.NoteSync

interface NotesStore {
    fun put(note: NoteSync)
    fun remove(id: String)
}
