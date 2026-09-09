package com.mudassar.notes.backend.domain.exception

sealed class DomainException(
    message: String,
    val errorCode: ErrorCode,
) : RuntimeException(message)

enum class ErrorCode(val value: String) {
    INVALID_CREDENTIALS("1006"),
    INVALID_REFRESH_TOKEN("1005"),
    MISSING_ACCESS_TOKEN("1004"),
    INVALID_ACCESS_TOKEN("1003"),
    INVALID_PLATFORM_HEADER("1002"),
    INTERNAL_ERROR("1001"),
}

class InvalidPlatformHeaderException :
    DomainException("Missing or unrecognized X-Platform header", errorCode = ErrorCode.INVALID_PLATFORM_HEADER)

class InvalidCredentialsException :
    DomainException("Invalid email or password", errorCode = ErrorCode.INVALID_CREDENTIALS)

class InvalidRefreshTokenException :
    DomainException("Invalid or expired refresh token", errorCode = ErrorCode.INVALID_REFRESH_TOKEN)

class MissingAccessTokenException :
    DomainException("Invalid or expired access token", errorCode = ErrorCode.MISSING_ACCESS_TOKEN)

class InvalidAccessTokenException :
    DomainException("Invalid or expired access token", errorCode = ErrorCode.INVALID_ACCESS_TOKEN)