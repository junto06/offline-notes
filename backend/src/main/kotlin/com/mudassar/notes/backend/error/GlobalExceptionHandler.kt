package com.mudassar.notes.backend.error

import com.mudassar.notes.backend.domain.exception.DomainException
import com.mudassar.notes.backend.domain.exception.EmailAlreadyInUseException
import com.mudassar.notes.backend.domain.exception.ErrorCode
import com.mudassar.notes.backend.domain.exception.InvalidAccessTokenException
import com.mudassar.notes.backend.domain.exception.InvalidCredentialsException
import com.mudassar.notes.backend.domain.exception.InvalidPlatformHeaderException
import com.mudassar.notes.backend.domain.exception.InvalidRefreshTokenException
import com.mudassar.notes.backend.domain.exception.MissingAccessTokenException
import com.mudassar.notes.backend.domain.exception.NoteNotFoundException
import com.mudassar.notes.backend.http.dto.ErrorResponseDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

// Generic errorhandler so we don't expose real stacktrace to client device
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(InvalidPlatformHeaderException::class)
    fun handleBadRequest(e: InvalidPlatformHeaderException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponseDto(message = e.message.orEmpty(), errorCode = e.errorCode.value))

    @ExceptionHandler(
        InvalidCredentialsException::class,
        InvalidRefreshTokenException::class,
        MissingAccessTokenException::class,
        InvalidAccessTokenException::class,
    )
    fun handleUnauthorized(e: DomainException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponseDto(message = e.message.orEmpty(), errorCode = e.errorCode.value))

    @ExceptionHandler(NoteNotFoundException::class)
    fun handleNotFound(e: NoteNotFoundException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponseDto(message = e.message.orEmpty(), errorCode = e.errorCode.value))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> {
        val message = e.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponseDto(message = message, errorCode = ErrorCode.VALIDATION_ERROR.value))
    }

    @ExceptionHandler(EmailAlreadyInUseException::class)
    fun handleEmailAlreadyInUse(e: EmailAlreadyInUseException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponseDto(message = e.message.orEmpty(), errorCode = e.errorCode.value))

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleRouteNotFound(e: NoResourceFoundException): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponseDto(message = "No such route", errorCode = ErrorCode.ROUTE_NOT_FOUND.value))

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponseDto> {
        log.error("Unhandled exception", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponseDto(
                    message = "Something went wrong. Please try again later.",
                    errorCode = ErrorCode.INTERNAL_ERROR.value,
                )
            )
    }
}