package com.mudassar.notes.common.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mudassar.notes.storage.DataStoreFactory
import com.mudassar.notes.storage.LocalDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealDataStoreFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) : DataStoreFactory {

    private val stores = ConcurrentHashMap<String, DataStore<Preferences>>()

    override fun create(storeName: String): LocalDataStore {
        val dataStore = stores.computeIfAbsent(storeName) {
            PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile(storeName)
            }
        }
        return PreferencesLocalDataStore(dataStore)
    }
}

private class PreferencesLocalDataStore(
    private val dataStore: DataStore<Preferences>,
) : LocalDataStore {

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        dataStore.data.first()[booleanPreferencesKey(key)] ?: defaultValue

    override suspend fun setBoolean(key: String, value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    override suspend fun remove(key: String) {
        dataStore.edit { it.remove(booleanPreferencesKey(key)) }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
