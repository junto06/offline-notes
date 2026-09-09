package com.mudassar.notes.backend.configurations

import com.mudassar.notes.backend.domain.exception.InvalidAccessTokenException
import com.mudassar.notes.backend.domain.exception.MissingAccessTokenException
import com.mudassar.notes.backend.domain.model.UserId
import com.mudassar.notes.backend.domain.repository.TokensRepository
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

interface UserContext : ClientContext {
    val userId: UserId
}

@Component
class UserContextResolver(
    private val tokensRepository: TokensRepository,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == UserContext::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): UserContext {
        val accessToken = webRequest.getHeader("Authorization")
            ?.removePrefix("Bearer ")
            ?.takeIf { it.isNotBlank() }
            ?: throw MissingAccessTokenException()

        val userId = tokensRepository.userIdFor(accessToken)
            ?: throw InvalidAccessTokenException()

        return RealUserContext(
            userId = userId,
            platform = webRequest.toPlatformContext(),
        )
    }
}

private class RealUserContext(
    override val userId: UserId,
    platform: ClientContext
) : UserContext, ClientContext by platform
