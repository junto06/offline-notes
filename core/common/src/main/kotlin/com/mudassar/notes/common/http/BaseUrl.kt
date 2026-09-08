package com.mudassar.notes.common.http

import javax.inject.Inject

class BaseUrl @Inject constructor() {
    operator fun invoke(): String {
        return "http://10.0.2.2:8080"
    }
}