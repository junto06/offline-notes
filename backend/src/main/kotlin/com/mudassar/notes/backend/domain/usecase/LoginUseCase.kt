package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.domain.repository.UsersRepository
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val usersRepository: UsersRepository,
) {
    operator fun invoke(email: String, password: String): User? =
        usersRepository.findByCredentials(email, password)
}
