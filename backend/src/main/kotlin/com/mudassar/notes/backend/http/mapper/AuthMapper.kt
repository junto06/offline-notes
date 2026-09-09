package com.mudassar.notes.backend.http.mapper

import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.domain.model.LoginResult
import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.http.dto.LoginResponseDto
import com.mudassar.notes.backend.http.dto.RefreshResponseDto
import com.mudassar.notes.backend.http.dto.SignupResponseDto

fun LoginResult.toDto(): LoginResponseDto = LoginResponseDto(
    id = user.id.value,
    name = user.name,
    accessToken = tokens.accessToken,
    refreshToken = tokens.refreshToken,
)

fun User.toDto(): SignupResponseDto = SignupResponseDto(
    id = id.value,
    email = email,
    name = name,
)

fun AuthTokens.toDto(): RefreshResponseDto = RefreshResponseDto(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
