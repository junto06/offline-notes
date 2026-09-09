package com.mudassar.notes.auth.remote

import com.mudassar.notes.auth.network.SkipAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Tag

interface AuthService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
        @Tag marker: SkipAuth = SkipAuth,
    ): LoginResponseDto

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequestDto,
        @Tag marker: SkipAuth = SkipAuth,
    ): Response<RefreshResponseDto>
}
