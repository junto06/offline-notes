package com.mudassar.notes.backend.configurations

import com.mudassar.notes.backend.domain.exception.InvalidPlatformHeaderException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class PlatformContextArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == ClientContext::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): ClientContext = webRequest.toPlatformContext()
}

internal fun NativeWebRequest.toPlatformContext(): ClientContext {
    val platform = getHeader("X-Platform").toPlatform()
        ?: throw InvalidPlatformHeaderException()

    val appVersion = getHeader("X-AppVersion").orEmpty()
    val language = getHeader("X-AppLanguage").toLanguage()

    return RealClientContext(
        appVersion = AppVersion(appVersion),
        platform = platform,
        language = language,
    )
}

private fun String?.toPlatform(): Platform? =
    Platform.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }

private fun String?.toLanguage(): Language {
    val primaryTag = this?.substringBefore('-')?.lowercase()
    return when (primaryTag) {
        "en" -> Language.English
        else -> Language.English // fall back rather than fail the request over a metadata header
    }
}

private class RealClientContext(
    override val appVersion: AppVersion,
    override val platform: Platform,
    override val language: Language,
): ClientContext