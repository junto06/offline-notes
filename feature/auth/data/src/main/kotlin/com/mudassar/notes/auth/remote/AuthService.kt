package com.mudassar.notes.auth.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
