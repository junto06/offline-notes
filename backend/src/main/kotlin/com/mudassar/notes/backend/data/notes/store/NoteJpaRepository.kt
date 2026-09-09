package com.mudassar.notes.backend.data.notes.store

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface NoteJpaRepository : JpaRepository<NoteEntity, String> {
    fun findAllByUserId(userId: String): List<NoteEntity>

    fun findByUserIdAndId(userId: String, id: String): NoteEntity?

    // Locks the row (or its absence) for the duration of the enclosing transaction, so a
    // concurrent update for the same user+note serializes instead of racing.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NoteEntity n where n.userId = :userId and n.id = :id")
    fun findForUpdate(userId: String, id: String): NoteEntity?
}
