package com.mudassar.notes.backend.util

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
class StringsProviderInit(messageSource: MessageSource) {

    init {
        StringsProvider.init(messageSource)
    }
}

object StringsProvider {
    private lateinit var messageSource: MessageSource

    fun init(source: MessageSource) {
        messageSource = source
    }

    fun getString(key: String, vararg args: Any): String =
        messageSource.getMessage(key, args, LocaleContextHolder.getLocale())
}