package com.mudassar.notes.backend.configurations

import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Configuration

// ClientContext is resolved by our own HandlerMethodArgumentResolver (headers), not bound from a
// single request value, without this springdoc lists the Kotlin-compiled extension receiver as
// a required query parameter on every endpoint.
@Configuration
class OpenApiConfig {
    init {
        SpringDocUtils
            .getConfig()
            .addRequestWrapperToIgnore(ClientContext::class.java)
    }
}
