package com.mudassar.notes.backend

import com.mudassar.notes.backend.http.dto.LoginRequestDto
import com.mudassar.notes.backend.http.dto.LoginResponseDto
import com.mudassar.notes.backend.http.dto.NoteDto
import com.mudassar.notes.backend.http.dto.NoteResponseDto
import com.mudassar.notes.backend.http.dto.ResolveConflictRequestDto
import com.mudassar.notes.backend.http.dto.SignupRequestDto
import com.mudassar.notes.backend.http.dto.SignupResponseDto
import com.mudassar.notes.backend.http.dto.SyncNotesResponseDto
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BackendIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    companion object {
        private val postgres = EmbeddedPostgres.start()

        @JvmStatic
        @DynamicPropertySource
        fun postgresProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.getJdbcUrl("postgres", "postgres") }
            registry.add("spring.datasource.username") { "postgres" }
            registry.add("spring.datasource.password") { "postgres" }
        }
    }

    private fun headers() = HttpHeaders().apply {
        set("X-Platform", "android")
    }

    @Test
    fun `full note lifecycle through real postgres`() {
        val email = "${UUID.randomUUID()}@example.com"
        val signupResponse = restTemplate.postForEntity(
            "/auth/signup",
            HttpEntity(SignupRequestDto(email = email, password = "password", name = "Test User"), headers()),
            SignupResponseDto::class.java,
        )
        assertThat(signupResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        val duplicateSignupResponse = restTemplate.postForEntity(
            "/auth/signup",
            HttpEntity(SignupRequestDto(email = email, password = "password", name = "Test User"), headers()),
            String::class.java,
        )
        assertThat(duplicateSignupResponse.statusCode).isEqualTo(HttpStatus.CONFLICT)

        val invalidSignupResponse = restTemplate.postForEntity(
            "/auth/signup",
            HttpEntity(SignupRequestDto(email = "not-an-email", password = "short", name = "Test User"), headers()),
            String::class.java,
        )
        assertThat(invalidSignupResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        val loginResponse = restTemplate.postForEntity(
            "/auth/login",
            HttpEntity(LoginRequestDto(email = email, password = "password"), headers()),
            LoginResponseDto::class.java,
        )
        assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.OK)
        val accessToken = requireNotNull(loginResponse.body).accessToken

        val authHeaders = headers().apply { setBearerAuth(accessToken) }

        val noteId = UUID.randomUUID().toString()
        val note = NoteDto(
            id = noteId,
            title = "Test title",
            content = "Test content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            operation = "UPDATE",
            version = 0,
        )
        val syncResponse = restTemplate.postForEntity(
            "/notes/sync",
            HttpEntity(listOf(note), authHeaders),
            SyncNotesResponseDto::class.java,
        )
        assertThat(syncResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(requireNotNull(syncResponse.body).versions[noteId]).isEqualTo(1L)

        val getAllResponse = restTemplate.exchange(
            "/notes",
            HttpMethod.GET,
            HttpEntity<Void>(authHeaders),
            Array<NoteResponseDto>::class.java,
        )
        assertThat(requireNotNull(getAllResponse.body).map { it.id }).contains(noteId)

        val resolveResponse = restTemplate.postForEntity(
            "/notes/$noteId/resolve",
            HttpEntity(ResolveConflictRequestDto(resolution = "KEEP_REMOTE", version = 1), authHeaders),
            NoteResponseDto::class.java,
        )
        assertThat(resolveResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(requireNotNull(resolveResponse.body).title).isEqualTo("Test title")
        assertThat(resolveResponse.body!!.deleted).isFalse()
    }

    @Test
    fun `delete is a tombstone, not a removal`() {
        val email = "${UUID.randomUUID()}@example.com"
        restTemplate.postForEntity(
            "/auth/signup",
            HttpEntity(SignupRequestDto(email = email, password = "password", name = "Test User"), headers()),
            SignupResponseDto::class.java,
        )
        val loginResponse = restTemplate.postForEntity(
            "/auth/login",
            HttpEntity(LoginRequestDto(email = email, password = "password"), headers()),
            LoginResponseDto::class.java,
        )
        val accessToken = requireNotNull(loginResponse.body).accessToken
        val authHeaders = headers().apply { setBearerAuth(accessToken) }

        val noteId = UUID.randomUUID().toString()
        val note = NoteDto(
            id = noteId,
            title = "To be deleted",
            content = "content",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            operation = "UPDATE",
            version = 0,
        )
        restTemplate.postForEntity(
            "/notes/sync",
            HttpEntity(listOf(note), authHeaders),
            SyncNotesResponseDto::class.java,
        )

        val deleteNote = note.copy(updatedAt = System.currentTimeMillis(), operation = "DELETE", version = 1)
        val deleteResponse = restTemplate.postForEntity(
            "/notes/sync",
            HttpEntity(listOf(deleteNote), authHeaders),
            SyncNotesResponseDto::class.java,
        )
        assertThat(requireNotNull(deleteResponse.body).conflicts).isEmpty()

        // The row is a tombstone, not gone - it still comes back from a fetch, flagged deleted.
        val getAllResponse = restTemplate.exchange(
            "/notes",
            HttpMethod.GET,
            HttpEntity<Void>(authHeaders),
            Array<NoteResponseDto>::class.java,
        )
        assertThat(requireNotNull(getAllResponse.body).first { it.id == noteId }.deleted).isTrue()
    }
}
