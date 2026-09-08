package com.mudassar.notes.backend.http

import com.mudassar.notes.backend.domain.usecase.SyncUseCase
import com.mudassar.notes.backend.http.dto.NoteDto
import com.mudassar.notes.backend.http.dto.SyncNotesResponseDto
import com.mudassar.notes.backend.http.dto.toDomain
import com.mudassar.notes.backend.http.dto.toDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notes")
class SyncController(
    private val syncUseCase: SyncUseCase,
) {
    @PostMapping("/sync")
    fun sync(@RequestBody notes: List<NoteDto>): SyncNotesResponseDto =
        syncUseCase(notes.map { it.toDomain() }).toDto()
}
