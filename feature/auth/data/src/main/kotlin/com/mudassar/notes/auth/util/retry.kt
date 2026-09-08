package com.mudassar.notes.auth.util

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T> withRetry(
    maxAttempts: Int = 3,
    initialDelayMillis: Long = 500,
    maxDelayMillis: Long = 10_000,
    delayFactor: Float = 2.0f,
    onFailure: suspend (Exception) -> Unit = {},
    block: suspend () -> T
): T {
    require(maxAttempts >= 1) { "maxAttempts must be >= 1" }

    var currentDelay = initialDelayMillis

    for (attempt in 0 until maxAttempts) {
        try {
            return block()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            onFailure(e)

            val isLastAttempt = attempt == maxAttempts - 1
            if (isLastAttempt || !e.shouldRetry()) {
                throw e
            }
        }

        delay(currentDelay.milliseconds)
        currentDelay = (currentDelay * delayFactor).toLong().coerceAtMost(maxDelayMillis)
    }

    error("Unreachable: maxAttempts is validated to be >= 1")
}

private fun Exception.shouldRetry(): Boolean {
    return when (this) {
        is IOException -> true
        is HttpException -> code() in retryableStatusCodes || isRetryable5xx()
        else -> false
    }
}

// Any 5xx is a transient except 501
private fun HttpException.isRetryable5xx(): Boolean =
    code() in 500..599 && code() != 501

private val retryableStatusCodes = setOf(408, 429)