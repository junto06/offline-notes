package com.mudassar.notes.common.di

import com.mudassar.notes.base.BuildInfo
import com.mudassar.notes.base.ClockProvider
import com.mudassar.notes.base.DefaultDispatcherProvider
import com.mudassar.notes.base.DispatcherProvider
import com.mudassar.notes.base.ErrorLogger
import com.mudassar.notes.base.LocaleProvider
import com.mudassar.notes.common.ClockProviderImpl
import com.mudassar.notes.common.LocaleProviderImpl
import com.mudassar.notes.common.http.BaseUrl
import com.mudassar.notes.common.http.CommonHeadersInterceptor
import com.mudassar.notes.common.navigation.RealNavigator
import com.mudassar.notes.common.observability.TimberErrorLogger
import com.mudassar.notes.common.storage.RealDataStoreFactory
import com.mudassar.notes.navigation.Navigator
import com.mudassar.notes.storage.DataStoreFactory
import dagger.Binds
import dagger.BindsOptionalOf
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.Optional
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
    fun bindLocaleProvider(impl: LocaleProviderImpl): LocaleProvider

    @Binds
    fun bindDataStoreFactory(impl: RealDataStoreFactory): DataStoreFactory

    @Binds
    fun bindErrorLogger(impl: TimberErrorLogger): ErrorLogger

    // allow feature and other modules (e.g. auth) contribute request interceptors
    @Multibinds
    fun bindInterceptors(): Set<Interceptor>

    @Binds
    @IntoSet
    fun bindCommonHeadersInterceptor(impl: CommonHeadersInterceptor): Interceptor

    // Let auth feature module supply a token-refresh
    // Authenticator without core depending on it
    @BindsOptionalOf
    fun bindAuthenticator(): Authenticator

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(
            interceptors: Set<@JvmSuppressWildcards Interceptor>,
            authenticator: Optional<Authenticator>,
        ): OkHttpClient {
            val builder = OkHttpClient.Builder()
            interceptors.forEach(builder::addInterceptor)
            authenticator.ifPresent(builder::authenticator)
            builder.addInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BODY })
            return builder.build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            baseUrl: BaseUrl,
            buildInfo: BuildInfo,
            okHttpClient: Lazy<OkHttpClient>,
            json: Json,
        ): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl(buildInfo.environment))
            .callFactory { request -> okHttpClient.get().newCall(request) }
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}