package com.mudassar.notes.sync.di

import com.mudassar.notes.sync.ScheduleFetch
import com.mudassar.notes.sync.ScheduleFetchImpl
import com.mudassar.notes.sync.ScheduleSync
import com.mudassar.notes.sync.ScheduleSyncImpl
import com.mudassar.notes.sync.SingleScheduleFetch
import com.mudassar.notes.sync.SingleScheduleFetchImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SyncModule {
    @Provides
    fun provideScheduleSync(impl: ScheduleSyncImpl): ScheduleSync = impl

    @Provides
    fun provideScheduleFetch(impl: ScheduleFetchImpl): ScheduleFetch = impl

    @Provides
    fun provideSingleScheduleFetch(impl: SingleScheduleFetchImpl): SingleScheduleFetch = impl
}
