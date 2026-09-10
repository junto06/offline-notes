package com.mudassar.notes.data.di

import android.content.Context
import androidx.room.Room
import com.mudassar.notes.data.local.NoteDao
import com.mudassar.notes.data.local.NoteDatabase
import com.mudassar.notes.data.remote.NotesService
import com.mudassar.notes.data.repository.NoteRepositoryImpl
import com.mudassar.notes.data.repository.SyncStateRepositoryImpl
import com.mudassar.notes.repository.NoteRepository
import com.mudassar.notes.repository.SyncStateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NoteModule {
    @Provides
    fun provideNoteRepository(repository: NoteRepositoryImpl): NoteRepository = repository

    @Provides
    fun provideSyncStateRepository(repository: SyncStateRepositoryImpl): SyncStateRepository = repository

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, "note_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()

    @Provides
    @Singleton
    fun provideNotesService(retrofit: Retrofit): NotesService = retrofit.create()
}
