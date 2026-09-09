package com.mudassar.notes.common.http

import javax.inject.Inject

class BaseUrl @Inject constructor() {
    operator fun invoke(): String {
        return "http://192.168.2.169:8080"
    }
}