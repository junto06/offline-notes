package com.mudassar.notes.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import java.io.File

interface DataStoreFactory {
    fun create(storeName: String): LocalDataStore
}

interface LocalDataStore {
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean
    suspend fun setBoolean(key: String, value: Boolean)
    suspend fun remove(key: String)
    suspend fun clear()
}

fun <T> Context.createDataStore(fileName: String, serializer: Serializer<T>): DataStore<T> =
    DataStoreFactory.create(serializer = serializer) {
        File(filesDir, "datastore/$fileName")
    }