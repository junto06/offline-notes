package com.mudassar.notes.backend.data.auth

import com.mudassar.notes.backend.domain.model.User

interface UsersStore {
    fun findByCredentials(email: String, password: String): User?

    // Returns null if [email] is already taken
    fun create(email: String, password: String, name: String): User?
}
