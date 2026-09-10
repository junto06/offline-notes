package com.mudassar.notes.base

import javax.inject.Inject

class BuildInfo @Inject constructor(
    val versionCode: Int,
    val version: String,
    val environment: Environment,
) {
    override fun toString(): String {
        return "BuildInfo(versionCode=$versionCode, version='$version', environment=$environment)"
    }
}

sealed interface Environment {
    data object Prod : Environment
    data class Staging(val overrideUrl: String) : Environment
}