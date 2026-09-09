package com.mudassar.notes.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SyncNotesResponseDto(
    val conflicts: Map<String, ConflictDto> = emptyMap(),
    val versions: Map<String, Long> = emptyMap(),
)
