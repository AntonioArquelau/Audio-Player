package com.example.audioplayer.di

import android.app.Application
import android.content.Context
import com.example.audioplayer.repository.SongRepositoryInterface
import com.example.audioplayer.repositoryimpl.FakeSongRepositoryImpl
import com.example.audioplayer.repositoryimpl.SongRepositoryImpl
import com.example.audioplayer.viewmodel.SongViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesSongRepository(context: Application): SongRepositoryInterface{
        return SongRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun providesSongViewModel(repository: SongRepositoryInterface): SongViewModel{
        return SongViewModel(repository)
    }
}