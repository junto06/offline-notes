package com.mudassar.notes.backend.domain.repository

import com.mudassar.notes.backend.domain.model.NoteSync

interface NotesRepository {
    fun save(note: NoteSync)
    fun delete(id: String)
}
