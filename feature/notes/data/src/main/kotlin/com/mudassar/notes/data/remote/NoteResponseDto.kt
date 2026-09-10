package com.mudassar.notes.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteResponseDto(
    val id: String,
    val title: String,
    val content: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    val version: Long,
    val deleted: Boolean,
)
