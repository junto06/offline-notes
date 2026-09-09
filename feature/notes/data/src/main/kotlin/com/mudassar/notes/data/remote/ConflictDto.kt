package com.mudassar.notes.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConflictDto(
    @SerialName("server_version") val serverVersion: Long,
    val reason: String,
)
