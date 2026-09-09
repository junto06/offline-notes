package com.mudassar.notes.repository

import com.mudassar.notes.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface SessionRepository {
    fun observeUser(): Flow<User?>
    suspend fun startSession(user: User)
    suspend fun clearSession()
}

suspend fun SessionRepository.getUser(): User? =
    observeUser().first()

suspend fun SessionRepository.isLoggedIn() =
    getUser() != null