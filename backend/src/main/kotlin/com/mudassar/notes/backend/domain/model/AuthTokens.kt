package com.mudassar.notes.backend.domain.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
