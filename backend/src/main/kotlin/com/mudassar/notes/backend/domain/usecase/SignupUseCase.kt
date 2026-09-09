package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.exception.EmailAlreadyInUseException
import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.domain.repository.UsersRepository
import org.springframework.stereotype.Service

// Only creates the account, login remains the single source of truth for issuing tokens.
@Service
class SignupUseCase(
    private val usersRepository: UsersRepository,
) {
    operator fun invoke(email: String, password: String, name: String): User =
        usersRepository.create(email, password, name) ?: throw EmailAlreadyInUseException()
}
