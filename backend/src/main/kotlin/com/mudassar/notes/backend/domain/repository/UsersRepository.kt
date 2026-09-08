package com.mudassar.notes.backend.domain.repository

import com.mudassar.notes.backend.domain.model.User

interface UsersRepository {
    fun findByCredentials(email: String, password: String): User?
}
