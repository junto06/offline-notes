package com.mudassar.notes.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SyncNotesResponseDto(
    val errors: Map<String, String> = emptyMap(),
)
