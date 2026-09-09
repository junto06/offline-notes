package com.mudassar.notes.backend.domain.model

data class LoginResult(
    val user: User,
    val tokens: AuthTokens,
)
