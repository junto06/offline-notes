package com.mudassar.notes.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ResolveConflictRequestDto(
    val resolution: String,
    val title: String? = null,
    val content: String? = null,
    val version: Long,
)
