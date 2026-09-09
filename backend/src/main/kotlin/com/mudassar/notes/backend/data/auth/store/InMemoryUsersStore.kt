package com.mudassar.notes.backend.data.auth.store

import com.mudassar.notes.backend.data.auth.UsersStore
import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.domain.model.UserId
import org.springframework.stereotype.Component

@Component
class InMemoryUsersStore : UsersStore {
    private val accounts =
        listOf(
            Account(
                email = "test@abc.com",
                password = "password",
                name = "Test User",
                id = "100"
            )
        ).associateBy { it.email }

    override fun findByCredentials(email: String, password: String): User? {
        val account = accounts[email] ?: return null
        if (account.password != password) return null
        return User(
            id = UserId(account.id),
            email = email,
            name = account.name
        )
    }

    private data class Account(
        val email: String,
        val password: String,
        val name: String,
        val id: String,
    )
}