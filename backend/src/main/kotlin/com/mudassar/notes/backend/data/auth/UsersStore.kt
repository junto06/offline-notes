package com.mudassar.notes.backend.data.auth

import com.mudassar.notes.backend.domain.model.User

interface UsersStore {
    fun findByCredentials(email: String, password: String): User?
}
