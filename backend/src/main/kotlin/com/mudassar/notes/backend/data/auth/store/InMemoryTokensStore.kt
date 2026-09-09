package com.mudassar.notes.backend.data.auth.store

import com.mudassar.notes.backend.data.auth.TokensStore
import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.util.ClockProvider
import com.mudassar.notes.backend.util.TokenProvider
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

private const val ACCESS_TOKEN_TTL_MINUTES = 15L
private const val REFRESH_TOKEN_TTL_DAYS = 30L

@Component
class InMemoryTokensStore(
    private val tokenProvider: TokenProvider,
    private val clockProvider: ClockProvider,
) : TokensStore {
    private val accessTokens = ConcurrentHashMap<String, Session>()
    private val refreshTokens = ConcurrentHashMap<String, Session>()

    override fun issue(userId: String): AuthTokens {
        val accessToken = tokenProvider()
        val refreshToken = tokenProvider()
        val now = clockProvider()
        accessTokens[accessToken] = generateSession(userId, now.plus(ACCESS_TOKEN_TTL_MINUTES, ChronoUnit.MINUTES))
        refreshTokens[refreshToken] = generateSession(userId, now.plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS))
        return AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    private fun generateSession(
        userId: String,
        now: Instant
    ): Session = Session(userId, now)

    override fun refresh(refreshToken: String): AuthTokens? {
        val session = refreshTokens.remove(refreshToken) ?: return null
        if (session.expiresAt.isBefore(clockProvider())) return null
        return issue(session.userId)
    }

    override fun userIdFor(accessToken: String): String? {
        val session = accessTokens[accessToken] ?: return null
        if (session.expiresAt.isBefore(clockProvider())) {
            accessTokens.remove(accessToken)
            return null
        }
        return session.userId
    }

    private data class Session(
        val userId: String,
        val expiresAt: Instant
    )
}
