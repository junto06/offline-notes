package com.mudassar.notes.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotesService {
    @GET("notes")
    suspend fun getAll(): List<NoteResponseDto>

    @POST("notes/sync")
    suspend fun syncNotes(@Body notes: List<NoteDto>): SyncNotesResponseDto

    @POST("notes/{id}/resolve")
    suspend fun resolveConflict(
        @Path("id") id: String,
        @Body request: ResolveConflictRequestDto,
    ): Response<NoteResponseDto>
}
