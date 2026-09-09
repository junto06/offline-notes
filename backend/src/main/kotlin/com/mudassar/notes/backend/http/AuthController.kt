package com.mudassar.notes.backend.http

import com.mudassar.notes.backend.configurations.ClientContext
import com.mudassar.notes.backend.domain.usecase.LoginUseCase
import com.mudassar.notes.backend.domain.usecase.RefreshTokenUseCase
import com.mudassar.notes.backend.http.dto.LoginRequestDto
import com.mudassar.notes.backend.http.dto.LoginResponseDto
import com.mudassar.notes.backend.http.dto.RefreshRequestDto
import com.mudassar.notes.backend.http.dto.RefreshResponseDto
import com.mudassar.notes.backend.http.mapper.toDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {
    @PostMapping("/login")
    fun ClientContext.login(@RequestBody request: LoginRequestDto): LoginResponseDto =
        loginUseCase(request.email, request.password).toDto()

    @PostMapping("/refresh")
    fun ClientContext.refresh(@RequestBody request: RefreshRequestDto): RefreshResponseDto =
        refreshTokenUseCase(request.refreshToken).toDto()
}
