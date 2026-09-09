package com.mudassar.notes.backend.util

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

private const val TOKEN_BYTES = 32

@Component
class TokenProvider {
    private val secureRandom = SecureRandom()

    operator fun invoke(): String {
        val bytes = ByteArray(TOKEN_BYTES)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
