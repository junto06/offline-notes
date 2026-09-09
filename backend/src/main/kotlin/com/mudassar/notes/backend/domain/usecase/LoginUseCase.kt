package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.exception.InvalidCredentialsException
import com.mudassar.notes.backend.domain.model.LoginResult
import com.mudassar.notes.backend.domain.repository.TokensRepository
import com.mudassar.notes.backend.domain.repository.UsersRepository
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val usersRepository: UsersRepository,
    private val tokensRepository: TokensRepository,
) {
    operator fun invoke(email: String, password: String): LoginResult {
        val user = usersRepository.findByCredentials(email, password)
            ?: throw InvalidCredentialsException()
        val tokens = tokensRepository.issue(user.id)
        return LoginResult(user = user, tokens = tokens)
    }
}
