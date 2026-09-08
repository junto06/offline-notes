package com.mudassar.notes.common.observability

import com.mudassar.notes.base.ErrorLogger
import timber.log.Timber
import javax.inject.Inject

class TimberErrorLogger @Inject constructor() : ErrorLogger {

    override fun logError(throwable: Throwable, message: String?) {
        if (message != null) Timber.e(throwable, message) else Timber.e(throwable)
    }
}
