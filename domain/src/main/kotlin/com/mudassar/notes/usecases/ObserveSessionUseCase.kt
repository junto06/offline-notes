package com.mudassar.notes.usecases

import com.mudassar.notes.models.User
import com.mudassar.notes.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    operator fun invoke(): Flow<User?> = repository.observeUser()
}
