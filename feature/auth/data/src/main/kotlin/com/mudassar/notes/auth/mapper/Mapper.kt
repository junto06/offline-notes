package com.mudassar.notes.auth.mapper

import com.mudassar.notes.auth.remote.LoginResponseDto
import com.mudassar.notes.models.User
import com.mudassar.notes.models.UserId

fun LoginResponseDto.toUser(): User = User(
    id = UserId(id),
    name = name,
    accessToken = accessToken,
    refreshToken = refreshToken
)
