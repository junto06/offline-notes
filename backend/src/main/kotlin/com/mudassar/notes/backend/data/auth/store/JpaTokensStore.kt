package com.mudassar.notes.backend.data.auth.store

import com.mudassar.notes.backend.data.auth.TokensStore
import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.util.ClockProvider
import com.mudassar.notes.backend.util.TokenHasher
import com.mudassar.notes.backend.util.TokenProvider
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.temporal.ChronoUnit

private const val ACCESS_TOKEN_TTL_MINUTES = 180L // 3 hours
private const val REFRESH_TOKEN_TTL_DAYS = 30L
private const val REAPER_INTERVAL_MS = 86_400_000L // 1 day

@Component
class JpaTokensStore(
    private val accessTokenRepository: AccessTokenJpaRepository,
    private val refreshTokenRepository: RefreshTokenJpaRepository,
    private val tokenProvider: TokenProvider,
    private val tokenHasher: TokenHasher,
    private val clockProvider: ClockProvider,
) : TokensStore {

    @Transactional
    override fun issue(userId: String): AuthTokens {
        val accessToken = tokenProvider()
        val refreshToken = tokenProvider()
        val now = clockProvider()
        accessTokenRepository.save(
            AccessTokenEntity(
                token = tokenHasher.hash(accessToken),
                userId = userId,
                expiresAt = now.plus(ACCESS_TOKEN_TTL_MINUTES, ChronoUnit.MINUTES)
            )
        )
        refreshTokenRepository.save(
            RefreshTokenEntity(
                token = tokenHasher.hash(refreshToken),
                userId = userId,
                expiresAt = now.plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS)
            )
        )
        // raw tokens go to the client, only their hashes are ever persisted
        return AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    @Transactional
    override fun refresh(refreshToken: String): AuthTokens? {
        val session = refreshTokenRepository.findByIdOrNull(tokenHasher.hash(refreshToken)) ?: return null
        refreshTokenRepository.delete(session)
        if (session.expiresAt.isBefore(clockProvider())) return null
        return issue(session.userId)
    }

    @Transactional
    override fun userIdFor(accessToken: String): String? {
        val session = accessTokenRepository.findByIdOrNull(tokenHasher.hash(accessToken)) ?: return null
        if (session.expiresAt.isBefore(clockProvider())) {
            accessTokenRepository.delete(session)
            return null
        }
        return session.userId
    }

    @Scheduled(fixedRate = REAPER_INTERVAL_MS)
    @Transactional
    fun cleanExpiredTokens() {
        val now = clockProvider()
        accessTokenRepository.deleteByExpiresAtBefore(now)
        refreshTokenRepository.deleteByExpiresAtBefore(now)
    }
}
