package com.mudassar.notes.backend.data.auth

import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.domain.repository.UsersRepository
import org.springframework.stereotype.Repository

@Repository
class UsersRepositoryImpl(
    private val usersStore: UsersStore,
) : UsersRepository {
    override fun findByCredentials(email: String, password: String): User? =
        usersStore.findByCredentials(email, password)
}
