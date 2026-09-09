package com.mudassar.notes.auth.network

import com.mudassar.notes.repository.SessionRepository
import com.mudassar.notes.repository.getUser
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionRepository: SessionRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { sessionRepository.getUser()?.accessToken }
        val request = if (accessToken == null || hasSkipTag(chain.request())) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        }
        return chain.proceed(request)
    }
}

internal fun hasSkipTag(request: Request) =
    request.tag(SkipAuth::class.java) != null
