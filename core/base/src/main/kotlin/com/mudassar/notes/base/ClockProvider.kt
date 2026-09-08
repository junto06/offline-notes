package com.mudassar.notes.base

import kotlin.time.Instant

interface ClockProvider {
    fun now(): Instant
}