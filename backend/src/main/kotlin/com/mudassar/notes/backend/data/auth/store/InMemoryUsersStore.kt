package com.mudassar.notes.backend.data.auth.store

import com.mudassar.notes.backend.data.auth.UsersStore
import com.mudassar.notes.backend.domain.model.User
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InMemoryUsersStore : UsersStore {
    private val accounts =
        listOf(Account(email = "test@abc.com", password = "password", name = "Test User"))
            .associateBy { it.email }

    override fun findByCredentials(email: String, password: String): User? {
        val account = accounts[email] ?: return null
        if (account.password != password) return null
        return User(
            id = UUID.randomUUID().toString(),
            email = email,
            name = account.name
        )
    }

    private data class Account(
        val email: String,
        val password: String,
        val name: String
    )
}