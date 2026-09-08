package com.mudassar.notes.common

import com.mudassar.notes.base.ClockProvider
import javax.inject.Inject
import kotlin.time.Instant

class ClockProviderImpl @Inject constructor(): ClockProvider {
    override fun now(): Instant {
        return Instant.fromEpochMilliseconds(System.currentTimeMillis())
    }
}