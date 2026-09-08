package com.mudassar.notes.common.di

import com.mudassar.notes.base.DefaultDispatcherProvider
import com.mudassar.notes.base.DispatcherProvider
import com.mudassar.notes.base.ClockProvider
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.common.ClockProviderImpl
import com.mudassar.notes.common.http.BaseUrl
import com.mudassar.notes.common.navigation.RealNavigator
import com.mudassar.notes.common.observability.TimberErrorLogger
import com.mudassar.notes.common.storage.RealDataStoreFactory
import com.mudassar.notes.navigation.Navigator
import com.mudassar.notes.storage.DataStoreFactory
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {
    @Binds
    fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider

    @Binds
    fun bindNavigator(impl: RealNavigator): Navigator

    @Binds
    fun bindTimeProvider(impl: ClockProviderImpl): ClockProvider

    @Binds
    fun bindDataStoreFactory(impl: RealDataStoreFactory): DataStoreFactory

    @Binds
    fun bindErrorLogger(impl: TimberErrorLogger): ErrorLogger

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .apply { level = HttpLoggingInterceptor.Level.BASIC })
                .build()

        @Provides
        @Singleton
        fun provideRetrofit(
            baseUrl: BaseUrl,
            okHttpClient: Lazy<OkHttpClient>,
            json: Json,
        ): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl())
            .callFactory { request -> okHttpClient.get().newCall(request) }
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}