package com.mudassar.notes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Upsert
    suspend fun upsertNotes(notes: List<NoteEntity>)

    // Skips any note whose id already exists locally - a pulled-from-server note must never
    // clobber a local row that might hold an unsynced (PENDING/ERROR/CONFLICT) edit.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotesIfAbsent(notes: List<NoteEntity>)

    @Query("DELETE FROM note WHERE id IN (:ids)")
    suspend fun deleteNotes(ids: List<String>)
}
