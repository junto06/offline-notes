package com.mudassar.notes.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface NotesService {
    @POST("notes/sync")
    suspend fun syncNotes(@Body notes: List<NoteDto>): SyncNotesResponseDto
}