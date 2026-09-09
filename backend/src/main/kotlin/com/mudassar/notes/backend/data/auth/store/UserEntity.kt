package com.mudassar.notes.backend.data.auth.store

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    val id: String,
    val email: String,
    @Column(name = "password_hash")
    val passwordHash: String,
    val name: String,
)
