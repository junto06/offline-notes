package com.mudassar.notes.repository

import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    fun observeNote(id: NoteId): Flow<Note?>
    suspend fun saveNote(note: Note)
    suspend fun syncNotes(): Boolean
}
