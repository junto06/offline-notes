package com.mudassar.notes.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun observeNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun observeNote(id: String): Flow<NoteEntity?>

    @Query("SELECT * FROM note WHERE status = :status")
    suspend fun getNotesByStatus(status: String): List<NoteEntity>

    @Query("SELECT * FROM note WHERE id IN (:ids)")
    suspend fun getNotesByIds(ids: List<String>): List<NoteEntity>

    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Upsert
    suspend fun upsertNotes(notes: List<NoteEntity>)

    @Query("DELETE FROM note WHERE id IN (:ids)")
    suspend fun deleteNotes(ids: List<String>)
}
