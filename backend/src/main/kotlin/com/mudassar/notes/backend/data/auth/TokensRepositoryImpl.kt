package com.mudassar.notes.backend.data.auth

import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.domain.model.UserId
import com.mudassar.notes.backend.domain.repository.TokensRepository
import org.springframework.stereotype.Repository

@Repository
class TokensRepositoryImpl(
    private val tokensStore: TokensStore,
) : TokensRepository {
    override fun issue(userId: UserId): AuthTokens = tokensStore.issue(userId.value)

    override fun refresh(refreshToken: String): AuthTokens? = tokensStore.refresh(refreshToken)

    override fun userIdFor(accessToken: String): UserId? =
        tokensStore.userIdFor(accessToken)?.let(::UserId)
}
