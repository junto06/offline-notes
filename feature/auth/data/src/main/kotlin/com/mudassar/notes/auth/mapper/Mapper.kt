package com.mudassar.notes.auth.mapper

import com.mudassar.notes.auth.remote.LoginResponseDto
import com.mudassar.notes.models.User

fun LoginResponseDto.toUser(): User = User(id = id, name = name)
