package com.mudassar.notes.data.remote

import kotlinx.serialization.Serializable

@Serializable
enum class NoteOperationDto {
    UPDATE,
    DELETE,
}
