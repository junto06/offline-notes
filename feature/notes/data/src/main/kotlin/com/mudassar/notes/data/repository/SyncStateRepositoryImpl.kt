package com.mudassar.notes.data.repository

import com.mudassar.notes.repository.SyncStateRepository
import com.mudassar.notes.storage.DataStoreFactory
import javax.inject.Inject

private const val STORE_NAME = "sync_state"
private const val KEY_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"

class SyncStateRepositoryImpl @Inject constructor(
    dataStoreFactory: DataStoreFactory,
) : SyncStateRepository {

    private val dataStore = dataStoreFactory.create(STORE_NAME)

    override suspend fun getLastSyncTimestamp(): Long =
        dataStore.getLong(KEY_LAST_SYNC_TIMESTAMP, defaultValue = 0)

    override suspend fun updateLastSyncTimestamp(timestamp: Long) {
        dataStore.setLong(KEY_LAST_SYNC_TIMESTAMP, timestamp)
    }

    override suspend fun clear() {
        dataStore.clear()
    }
}
