package com.mudassar.notes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val status: String,
    val failureReason: String?,
    val deleted: Boolean,
)
