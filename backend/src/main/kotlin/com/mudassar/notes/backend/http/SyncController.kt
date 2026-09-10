package com.mudassar.notes.backend.http

import com.mudassar.notes.backend.configurations.BEARER_AUTH_SCHEME
import com.mudassar.notes.backend.configurations.UserContext
import com.mudassar.notes.backend.domain.model.ResolveNotesConflictOutcome
import com.mudassar.notes.backend.domain.usecase.GetNoteUseCase
import com.mudassar.notes.backend.domain.usecase.GetNotesUseCase
import com.mudassar.notes.backend.domain.usecase.ResolveConflictUseCase
import com.mudassar.notes.backend.domain.usecase.SyncUseCase
import com.mudassar.notes.backend.http.dto.ConflictDto
import com.mudassar.notes.backend.http.dto.NoteDto
import com.mudassar.notes.backend.http.dto.NoteResponseDto
import com.mudassar.notes.backend.http.dto.ResolveConflictRequestDto
import com.mudassar.notes.backend.http.dto.SyncNotesResponseDto
import com.mudassar.notes.backend.http.mapper.toDomain
import com.mudassar.notes.backend.http.mapper.toDto
import com.mudassar.notes.backend.http.mapper.toResponseDto
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notes")
class SyncController(
    private val syncUseCase: SyncUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val resolveConflictUseCase: ResolveConflictUseCase,
) {
    @GetMapping
    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    fun UserContext.getAll(@RequestParam(required = false) since: Long?): List<NoteResponseDto> =
        getNotesUseCase(userId, since).map { it.toResponseDto() }

    // Conditional fetch, pass the version already held locally and get 204 (nobody)
    // back if nothing changed, instead of re-transferring a note that's already up to date.
    @GetMapping("/{id}")
    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    fun UserContext.getById(
        @PathVariable id: String,
        @RequestParam(required = false) version: Long?,
    ): ResponseEntity<NoteResponseDto> {
        val note = getNoteUseCase(userId, id) ?: return ResponseEntity.notFound().build()
        if (version != null && version == note.version) {
            // 204 NoContent
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(note.toResponseDto())
    }

    @PostMapping("/sync")
    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    fun UserContext.sync(@RequestBody notes: List<NoteDto>): SyncNotesResponseDto =
        syncUseCase(notes.map { it.toDomain(userId) }).toDto()

    @PostMapping("/{id}/resolve")
    @SecurityRequirement(name = BEARER_AUTH_SCHEME)
    fun UserContext.resolveConflict(
        @PathVariable id: String,
        @RequestBody request: ResolveConflictRequestDto,
    ): ResponseEntity<*> =
        when (val outcome = resolveConflictUseCase(request.toDomain(userId, id))) {
            is ResolveNotesConflictOutcome.Resolved ->
                ResponseEntity.ok(outcome.note.toResponseDto())

            is ResolveNotesConflictOutcome.Conflict ->
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ConflictDto(serverVersion = outcome.serverVersion, reason = outcome.reason))
        }
}
