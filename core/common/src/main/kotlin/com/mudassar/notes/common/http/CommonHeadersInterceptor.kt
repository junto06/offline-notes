package com.mudassar.notes.common.http

import com.mudassar.notes.base.BuildInfo
import com.mudassar.notes.base.LocaleProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class CommonHeadersInterceptor @Inject constructor(
    private val buildInfo: BuildInfo,
    private val localeProvider: LocaleProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("X-AppLanguage", localeProvider().toLanguageTag())
            .header("X-AppVersion", buildInfo.version)
            .header("X-Platform", "android")
            .build()
        return chain.proceed(request)
    }
}
