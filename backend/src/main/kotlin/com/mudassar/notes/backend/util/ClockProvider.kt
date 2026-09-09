package com.mudassar.notes.backend.util

import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ClockProvider {
    operator fun invoke(): Instant = Instant.now()
}
