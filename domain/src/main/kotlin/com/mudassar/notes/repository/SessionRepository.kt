package com.mudassar.notes.repository

import com.mudassar.notes.models.User
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeUser(): Flow<User?>
    suspend fun startSession(user: User)
}
