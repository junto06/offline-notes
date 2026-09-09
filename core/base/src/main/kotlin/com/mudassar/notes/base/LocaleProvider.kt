package com.mudassar.notes.base

import java.util.Locale

interface LocaleProvider {
    operator fun invoke(): Locale
}
