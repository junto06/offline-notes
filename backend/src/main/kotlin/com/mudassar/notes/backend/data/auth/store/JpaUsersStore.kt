package com.mudassar.notes.backend.data.auth.store

import com.mudassar.notes.backend.data.auth.UsersStore
import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.domain.model.UserId
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JpaUsersStore(
    private val userJpaRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
) : UsersStore {
    override fun findByCredentials(email: String, password: String): User? {
        val account = userJpaRepository.findByEmail(email) ?: return null
        if (!passwordEncoder.matches(password, account.passwordHash)) return null
        return User(
            id = UserId(account.id),
            email = account.email,
            name = account.name
        )
    }

    override fun create(email: String, password: String, name: String): User? {
        if (userJpaRepository.findByEmail(email) != null) return null
        val id = UUID.randomUUID().toString()
        return try {
            userJpaRepository.save(
                UserEntity(
                    id = id,
                    email = email,
                    passwordHash = passwordEncoder.encode(password),
                    name = name
                )
            )
            User(
                id = UserId(id),
                email = email,
                name = name
            )
        } catch (e: DataIntegrityViolationException) {
            // lost a race with a concurrent signup for the same email
            // the unique constraint on `email` is the real guard
            null
        }
    }
}
