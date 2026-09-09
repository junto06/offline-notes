package com.mudassar.notes.backend.domain.repository

import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.domain.model.UserId

interface TokensRepository {
    fun issue(userId: UserId): AuthTokens

    // Returns a new token pair if [refreshToken] is valid
    // and unexpired, or null otherwise
    fun refresh(refreshToken: String): AuthTokens?

    // Returns the owning user id if [accessToken] is valid and unexpired, or null otherwise
    fun userIdFor(accessToken: String): UserId?
}
