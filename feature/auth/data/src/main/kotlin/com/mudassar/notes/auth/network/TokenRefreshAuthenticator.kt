package com.mudassar.notes.auth.network

import com.mudassar.notes.auth.remote.AuthService
import com.mudassar.notes.auth.remote.RefreshRequestDto
import com.mudassar.notes.auth.remote.RefreshResponseDto
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.models.User
import com.mudassar.notes.repository.SessionRepository
import com.mudassar.notes.repository.getUser
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Provider

private const val MAX_ATTEMPTS = 1

// On 401, exchange the refresh token for a new one and retry request.
// This authenticator is installed on the same OkHttpClient that AuthService's
// Retrofit instance calls through.
class TokenRefreshAuthenticator @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authServiceProvider: Provider<AuthService>,
    private val errorLogger: ErrorLogger,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Never try to refresh login/refresh endpoints itself marked via @Tag on AuthService
        if (hasSkipTag(response.request)) return null
        if (responseCount(response) > MAX_ATTEMPTS) return null

        return synchronized(this) {
            val currentUser = runBlocking { sessionRepository.getUser() } ?: return@synchronized null

            if (isAlreadyRefreshed(currentUser.accessToken, response.request)) {
                // A concurrent request already refreshed the session while we waited for the lock.
                return@synchronized response.retryWithAccessToken(currentUser.accessToken)
            }

            val refreshResponse = runCatching {
                refreshToken(currentUser)
            }.getOrElse { e ->
                // may just be transient error (no connectivity, timeout, ...), leave as it is
                errorLogger.logError(e, "Token refresh failed: could not reach server")
                return@synchronized null
            }

            if (refreshResponse.code() == 401) {
                // The server confirmed the refresh token itself is dead
                // the session can't be recovered so we must remove user session
                errorLogger.logError(HttpException(refreshResponse), "Refresh token invalid or expired")
                runBlocking { sessionRepository.clearSession() }
                return@synchronized null
            }

            val newTokens = refreshResponse.body()?.takeIf { refreshResponse.isSuccessful } ?: run {
                // may just be transient, leave as it
                errorLogger.logError(HttpException(refreshResponse), "Token refresh failed")
                return@synchronized null
            }

            runBlocking {
                sessionRepository.startSession(
                    currentUser.copy(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken
                    )
                )
            }

            response.retryWithAccessToken(newTokens.accessToken)
        }
    }

    private fun refreshToken(currentUser: User): retrofit2.Response<RefreshResponseDto> = runBlocking {
        authServiceProvider.get().refresh(RefreshRequestDto(currentUser.refreshToken))
    }

    private fun Response.retryWithAccessToken(accessToken: String): Request =
        request.newBuilder().header("Authorization", "Bearer $accessToken").build()

    // True if the request that just failed was already sent with a
    // different access token than the one currently in the session
    private fun isAlreadyRefreshed(currentAccessToken: String, failedRequest: Request): Boolean {
        val failedAccessToken = failedRequest.header("Authorization")?.removePrefix("Bearer ")
        return currentAccessToken != failedAccessToken
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
