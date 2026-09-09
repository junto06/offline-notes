package com.mudassar.notes.backend.data.auth.store

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "access_tokens")
class AccessTokenEntity(
    @Id
    val token: String,
    @Column(name = "user_id")
    val userId: String,
    @Column(name = "expires_at")
    val expiresAt: Instant,
)
