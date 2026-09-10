package com.mudassar.notes.di

import com.mudassar.notes.BuildConfig
import com.mudassar.notes.BuildConfig.STAGING_OVERRIDE_URL
import com.mudassar.notes.base.BuildInfo
import com.mudassar.notes.base.Environment
import com.mudassar.notes.base.Environment.Prod
import com.mudassar.notes.base.Environment.Staging
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
    fun provideBuildInfo(): BuildInfo {
        return BuildInfo(
            versionCode = BuildConfig.VERSION_CODE,
            version = BuildConfig.VERSION_NAME,
            environment = environment(),
        )
    }

    private fun environment(): Environment {
        // don't change STAGING_OVERRIDE_URL type
        return STAGING_OVERRIDE_URL?.let { Staging(it) } ?: Prod
    }
}