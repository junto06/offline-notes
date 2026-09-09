package com.mudassar.notes.di

import com.mudassar.notes.BuildConfig
import com.mudassar.notes.base.BuildInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideBuildInfo(): BuildInfo = BuildInfo(
        versionCode = BuildConfig.VERSION_CODE,
        version = BuildConfig.VERSION_NAME,
    )
}