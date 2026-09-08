package com.mudassar.notes.repository

import com.mudassar.notes.models.DomainResult
import com.mudassar.notes.models.User

interface UserRepository {
    suspend fun login(email: String, password: String): DomainResult<User>
}