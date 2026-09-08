package com.mudassar.notes.backend.http.mapper

import com.mudassar.notes.backend.domain.model.User
import com.mudassar.notes.backend.http.dto.LoginResponseDto

fun User.toDto(): LoginResponseDto = LoginResponseDto(id = id, name = name)
