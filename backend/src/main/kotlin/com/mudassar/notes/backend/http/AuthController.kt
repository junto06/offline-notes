package com.mudassar.notes.backend.http

import com.mudassar.notes.backend.domain.usecase.LoginUseCase
import com.mudassar.notes.backend.http.dto.LoginRequestDto
import com.mudassar.notes.backend.http.dto.LoginResponseDto
import com.mudassar.notes.backend.http.mapper.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDto): LoginResponseDto {
        val user = loginUseCase(request.email, request.password)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        return user.toDto()
    }
}
