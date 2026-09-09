package com.mudassar.notes.backend.http.dto

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class LoginResponseDto(
    val id: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshRequestDto(
    val refreshToken: String,
)

data class RefreshResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
