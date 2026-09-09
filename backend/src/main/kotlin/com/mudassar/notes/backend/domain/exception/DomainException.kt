package com.mudassar.notes.backend.domain.exception

sealed class DomainException(
    message: String,
    val errorCode: ErrorCode,
) : RuntimeException(message)

enum class ErrorCode(val value: String) {
    INVALID_PLATFORM_HEADER("1002"),
    INTERNAL_ERROR("1001"),
}

class InvalidPlatformHeaderException :
    DomainException("Missing or unrecognized X-Platform header", errorCode = ErrorCode.INVALID_PLATFORM_HEADER)
