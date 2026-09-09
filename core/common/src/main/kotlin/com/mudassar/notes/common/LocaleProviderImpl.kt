package com.mudassar.notes.common

import com.mudassar.notes.base.LocaleProvider
import java.util.Locale
import javax.inject.Inject

class LocaleProviderImpl @Inject constructor() : LocaleProvider {
    override fun invoke(): Locale = Locale.getDefault()
}
