package com.mudassar.notes

import com.mudassar.notes.base.HasErrorLogger
import com.mudassar.notes.models.DomainResult
import com.mudassar.notes.models.ErrorType
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

context(logger: HasErrorLogger)
suspend inline fun <T> safeCall(
    crossinline errorMapper: (Throwable) -> ErrorType = { it.toDomainError() },
    crossinline block: suspend () -> T
): DomainResult<T> = try {
    DomainResult.Success(block())
} catch (e: Exception) {
    if (e is CancellationException) throw e
    logger.errorLogger.logError(e)
    DomainResult.Error(errorMapper(e))
}

fun Throwable.toDomainError(): ErrorType {
    return when (this) {
        is IOException -> ErrorType.NetworkError
        else -> ErrorType.UnknownError
    }
}