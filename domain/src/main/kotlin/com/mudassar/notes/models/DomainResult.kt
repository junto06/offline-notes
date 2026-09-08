package com.mudassar.notes.models

sealed class DomainResult<out T> {
    data class Success<T>(val data: T): DomainResult<T>()
    data class Error(val type: ErrorType): DomainResult<Nothing>()
}

sealed interface ErrorType {
    object NetworkError: ErrorType
    object UnknownError: ErrorType

    object Conflict: ErrorType
}