package com.mudassar.notes.models

sealed interface ResolveConflictResult {
    data class Resolved(val note: Note) : ResolveConflictResult
    data class StillConflicting(val serverVersion: Long, val reason: String) : ResolveConflictResult
    data object Failed : ResolveConflictResult
}
