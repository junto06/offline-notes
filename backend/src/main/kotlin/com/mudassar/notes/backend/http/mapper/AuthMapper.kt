package com.mudassar.notes.backend.http.mapper

import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.domain.model.LoginResult
import com.mudassar.notes.backend.http.dto.LoginResponseDto
import com.mudassar.notes.backend.http.dto.RefreshResponseDto

fun LoginResult.toDto(): LoginResponseDto = LoginResponseDto(
    id = user.id.value,
    name = user.name,
    accessToken = tokens.accessToken,
    refreshToken = tokens.refreshToken,
)

fun AuthTokens.toDto(): RefreshResponseDto = RefreshResponseDto(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
