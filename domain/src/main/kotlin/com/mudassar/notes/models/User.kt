package com.mudassar.notes.models

data class User(
    val id: UserId,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
)

@JvmInline
value class UserId(val value: String)