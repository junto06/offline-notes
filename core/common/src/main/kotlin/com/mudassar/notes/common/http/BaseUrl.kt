package com.mudassar.notes.common.http

import com.mudassar.notes.base.Environment
import javax.inject.Inject

class BaseUrl @Inject constructor() {
    operator fun invoke(environment: Environment): String = when (environment) {
        Environment.Prod -> "https://p01--notesapp--bh5gk4hygqyb.code.run"
        is Environment.Staging -> environment.overrideUrl
    }
}