package com.mudassar.notes.backend.configurations

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val BEARER_AUTH_SCHEME = "bearerAuth"

// UserContext/ClientContext are resolved by our own HandlerMethodArgumentResolvers (headers +
// access token), not bound from a single request value - without this, springdoc lists the
// Kotlin-compiled extension receiver as a required query parameter on every endpoint.
@Configuration
class OpenApiConfig {
    init {
        SpringDocUtils
            .getConfig()
            .addRequestWrapperToIgnore(
                UserContext::class.java,
                ClientContext::class.java
            )
    }

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .components(
            Components().addSecuritySchemes(
                BEARER_AUTH_SCHEME,
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("opaque access token from /auth/login or /auth/refresh"),
            )
        )

    // Every endpoint takes a ClientContext (X-Platform/X-AppVersion/X-AppLanguage), resolved from
    // headers rather than a documentable request param - add them to every operation manually.
    @Bean
    fun clientContextHeaders(): OperationCustomizer =
        OperationCustomizer { operation, _ ->
            operation
                .addParametersItem(headerParameter("X-Platform", required = true, defaultValue = "android"))
                .addParametersItem(headerParameter("X-AppVersion", required = false, defaultValue = "1.0"))
                .addParametersItem(headerParameter("X-AppLanguage", required = false, defaultValue = "en"))
        }

    private fun headerParameter(name: String, required: Boolean, defaultValue: String): Parameter =
        Parameter()
            .`in`("header")
            .name(name)
            .required(required)
            .schema(StringSchema()._default(defaultValue))
}
