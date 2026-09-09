package com.mudassar.notes.backend.domain.usecase

import com.mudassar.notes.backend.domain.exception.InvalidRefreshTokenException
import com.mudassar.notes.backend.domain.model.AuthTokens
import com.mudassar.notes.backend.domain.repository.TokensRepository
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCase(
    private val tokensRepository: TokensRepository,
) {
    operator fun invoke(refreshToken: String): AuthTokens =
        tokensRepository.refresh(refreshToken) ?: throw InvalidRefreshTokenException()
}
