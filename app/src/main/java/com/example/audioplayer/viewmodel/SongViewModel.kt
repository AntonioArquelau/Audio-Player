package com.example.audioplayer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.audioplayer.model.Song
import com.example.audioplayer.repository.SongRepositoryInterface
import com.example.audioplayer.repositoryimpl.FakeSongRepositoryImpl

class SongViewModel: ViewModel() {
    companion object{
        const val TAG = "SongViewModel"
    }

    private val songRepository: SongRepositoryInterface by lazy {
        FakeSongRepositoryImpl()
    }

    fun getSongList(): List<Song>{
        return songRepository.getSongsList()
    }

    fun playSong() {
        songRepository.play()
    }

    fun pauseSong(){
        songRepository.pause()
    }

    fun repeatSong(enable: Boolean){
        songRepository.repeat(enable)
    }

    fun randomSong(enable: Boolean){
        songRepository.random(enable)
    }

    fun nextSong(){
        songRepository.next()
    }

    fun previousSong(){
        songRepository.previous()
    }
}