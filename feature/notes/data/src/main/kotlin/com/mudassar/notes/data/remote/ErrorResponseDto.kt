package com.mudassar.notes.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val message: String? = null,
)
