package com.mudassar.notes.auth.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.mudassar.notes.auth.network.AuthInterceptor
import com.mudassar.notes.auth.network.TokenRefreshAuthenticator
import com.mudassar.notes.auth.remote.AuthService
import com.mudassar.notes.auth.repository.SessionRepositoryImpl
import com.mudassar.notes.auth.repository.UserRepositoryImpl
import com.mudassar.notes.auth.repository.UserSessionSerializer
import com.mudassar.notes.models.User
import com.mudassar.notes.repository.SessionRepository
import com.mudassar.notes.repository.UserRepository
import com.mudassar.notes.storage.createDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Authenticator
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    fun provideUserRepository(repository: UserRepositoryImpl): UserRepository = repository

    @Provides
    fun provideSessionRepository(repository: SessionRepositoryImpl): SessionRepository = repository

    @Provides
    @IntoSet
    fun provideAuthInterceptor(interceptor: AuthInterceptor): Interceptor = interceptor

    @Provides
    fun provideTokenRefreshAuthenticator(authenticator: TokenRefreshAuthenticator): Authenticator = authenticator

    @Provides
    @Singleton
    fun provideUserSessionDataStore(
        @ApplicationContext context: Context,
        serializer: UserSessionSerializer,
    ): DataStore<User?> = context.createDataStore("user_session.json", serializer)

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create()
}
