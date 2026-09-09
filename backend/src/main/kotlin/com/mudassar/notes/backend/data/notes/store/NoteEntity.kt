package com.mudassar.notes.backend.data.notes.store

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "notes")
class NoteEntity(
    @Id
    val id: String,
    @Column(name = "user_id")
    val userId: String,
    val title: String,
    val content: String,
    @Column(name = "created_at")
    val createdAt: Long,
    @Column(name = "updated_at")
    val updatedAt: Long,
    val version: Long,
)
