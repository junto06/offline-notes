package com.mudassar.notes.backend.http.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequestDto(
    val email: String,
    val password: String,
)

data class SignupRequestDto(
    @field:NotBlank
    @field:Email
    val email: String,
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    @field:NotBlank
    val name: String,
)

data class LoginResponseDto(
    val id: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
)

data class SignupResponseDto(
    val id: String,
    val email: String,
    val name: String,
)

data class RefreshRequestDto(
    val refreshToken: String,
)

data class RefreshResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
