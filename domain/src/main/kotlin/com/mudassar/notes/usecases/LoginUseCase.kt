package com.mudassar.notes.usecases

import com.mudassar.notes.models.DomainResult
import com.mudassar.notes.models.User
import com.mudassar.notes.repository.SessionRepository
import com.mudassar.notes.repository.UserRepository
import com.mudassar.notes.sync.ScheduleFetch
import com.mudassar.notes.sync.ScheduleSync
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val scheduleSync: ScheduleSync,
    private val scheduleFetch: ScheduleFetch,
) {
    suspend operator fun invoke(email: String, password: String): DomainResult<User> {
        val result = repository.login(email, password)
        if (result is DomainResult.Success) {
            sessionRepository.startSession(result.data)
            scheduleFetch.schedule() // pull notes from other devices
            scheduleSync.schedule() // push any pending notes
        }
        return result
    }
}
