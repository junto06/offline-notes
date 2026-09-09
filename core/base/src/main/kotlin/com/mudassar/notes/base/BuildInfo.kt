package com.mudassar.notes.base

import javax.inject.Inject

class BuildInfo @Inject constructor(
    val versionCode: Int,
    val version: String,
) {
    override fun toString(): String {
        return "BuildInfo(versionCode=$versionCode, version='$version')"
    }
}