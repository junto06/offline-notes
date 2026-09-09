package com.mudassar.notes.backend.configurations

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val userContextResolver: UserContextResolver,
    private val platformContextArgumentResolver: PlatformContextArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userContextResolver)
        resolvers.add(platformContextArgumentResolver)
    }
}
