package com.mudassar.notes.backend.data.auth.store

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, String> {
    fun deleteByExpiresAtBefore(instant: Instant): Long
}
