package com.mudassar.notes.base.util

import kotlin.enums.enumEntries

inline fun <reified T : Enum<T>> String.byName() =
    enumEntries<T>().first { it.name == this }