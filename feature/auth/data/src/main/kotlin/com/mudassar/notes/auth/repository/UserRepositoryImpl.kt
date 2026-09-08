package com.mudassar.notes.auth.repository

import com.mudassar.notes.auth.mapper.toUser
import com.mudassar.notes.auth.remote.AuthService
import com.mudassar.notes.auth.remote.LoginRequestDto
import com.mudassar.notes.auth.util.withRetry
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.base.HasErrorLogger
import com.mudassar.notes.models.DomainResult
import com.mudassar.notes.models.User
import com.mudassar.notes.repository.UserRepository
import com.mudassar.notes.safeCall
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    override val errorLogger: ErrorLogger,
) : UserRepository, HasErrorLogger {

    override suspend fun login(email: String, password: String): DomainResult<User> =
        safeCall {
            withRetry(maxAttempts = 2) {
                val request = LoginRequestDto(email = email, password = password)
                authService.login(request)
                    .toUser()
            }
        }
}
