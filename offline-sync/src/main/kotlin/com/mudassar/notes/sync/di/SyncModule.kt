package com.mudassar.notes.sync.di

import com.mudassar.notes.sync.ScheduleSync
import com.mudassar.notes.sync.ScheduleSyncImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SyncModule {
    @Provides
    fun provideScheduleSync(impl: ScheduleSyncImpl): ScheduleSync = impl
}
