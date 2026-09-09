package com.mudassar.notes.backend.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerMapping

@Component
class RequestLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.requestURI.startsWith("/actuator")

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val method = request.method
        // path only, deliberately never the query string: a future endpoint could carry a
        // token/secret as a query param, and that would otherwise land in server logs
        val path = request.requestURI
        log.debug("--> {} {}", method, path)

        val startedAt = System.currentTimeMillis()
        try {
            filterChain.doFilter(request, response)
        } finally {
            val durationMs = System.currentTimeMillis() - startedAt
            // resolved only once the handler is matched, so unavailable at request-start; falls back to
            // the raw path when nothing matched (e.g. a genuine 404), which is exactly when the raw path matters
            val endpoint = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) as? String ?: path
            log.info("<-- {} {} {} ({} ms)", method, endpoint, response.status, durationMs)
        }
    }
}