package com.mudassar.notes.backend.http.dto

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class LoginResponseDto(
    val id: String,
    val name: String,
)
