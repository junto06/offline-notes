package com.mudassar.notes.base

interface ErrorLogger {
    fun logError(throwable: Throwable, message: String? = null)
}

interface HasErrorLogger {
    val errorLogger: ErrorLogger
}