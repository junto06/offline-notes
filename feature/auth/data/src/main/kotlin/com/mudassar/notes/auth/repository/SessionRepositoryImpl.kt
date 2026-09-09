package com.mudassar.notes.auth.repository

import androidx.datastore.core.DataStore
import com.mudassar.notes.models.User
import com.mudassar.notes.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<User?>,
) : SessionRepository {

    override fun observeUser(): Flow<User?> = dataStore.data

    override suspend fun startSession(user: User) {
        dataStore.updateData { user }
    }

    override suspend fun clearSession() {
        dataStore.updateData { null }
    }
}
