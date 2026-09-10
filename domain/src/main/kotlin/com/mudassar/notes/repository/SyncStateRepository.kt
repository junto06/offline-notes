package com.mudassar.notes.repository

interface SyncStateRepository {
    suspend fun getLastSyncTimestamp(): Long
    suspend fun updateLastSyncTimestamp(timestamp: Long)
    suspend fun clear()
}
