package com.mudassar.notes.backend.util

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TokenProvider {
    operator fun invoke(): String = UUID.randomUUID().toString()
}
