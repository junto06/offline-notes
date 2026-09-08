package com.mudassar.notes.auth.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)
