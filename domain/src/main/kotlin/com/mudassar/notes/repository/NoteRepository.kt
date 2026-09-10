package com.mudassar.notes.repository

import com.mudassar.notes.models.ConflictResolution
import com.mudassar.notes.models.Note
import com.mudassar.notes.models.NoteId
import com.mudassar.notes.models.ResolveConflictResult
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    fun observeNote(id: NoteId): Flow<Note?>
    suspend fun saveNote(note: Note)
    suspend fun syncNotes(): Boolean
    suspend fun fetchNotes(): Boolean
    suspend fun refreshNote(id: NoteId): Boolean
    suspend fun resolveConflict(note: Note, resolution: ConflictResolution): ResolveConflictResult
}
