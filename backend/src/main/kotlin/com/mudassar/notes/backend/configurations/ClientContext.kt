package com.mudassar.notes.backend.configurations

interface ClientContext {
    val appVersion: AppVersion
    val platform: Platform
    val language: Language
}

@JvmInline
value class AppVersion(val value: String)

enum class Platform {
    Android,
    IOS
}

enum class Language {
    English
}
