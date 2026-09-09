package com.mudassar.notes.backend.domain.model

data class User(
    val id: UserId,
    val email: String,
    val name: String,
)

@JvmInline
value class UserId(val value: String)