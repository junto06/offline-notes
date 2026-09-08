package com.mudassar.notes.auth.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val id: String,
    val name: String,
)
