package com.mudassar.notes.backend.data.auth

import com.mudassar.notes.backend.domain.model.AuthTokens

interface TokensStore {
    fun issue(userId: String): AuthTokens

    // Returns a new token pair if [refreshToken] is valid
    // and unexpired, or null otherwise
    fun refresh(refreshToken: String): AuthTokens?

    // Returns the owning user id if [accessToken] is
    // valid and unexpired, or null otherwise
    fun userIdFor(accessToken: String): String?
}
