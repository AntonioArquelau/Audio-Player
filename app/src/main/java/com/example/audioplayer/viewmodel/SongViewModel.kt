package com.example.audioplayer.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.audioplayer.model.Song
import com.example.audioplayer.repository.SongRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepositoryInterface
): ViewModel() {
    companion object{
        const val TAG = "SongViewModel"
    }

    fun getSongList(): List<Song>{
        return songRepository.getSongsList()
    }

    fun createService(position: Int, intent: Intent){
        songRepository.createService(position, intent)
    }

    fun play(){
        songRepository.play()
    }
     fun pause(){
         songRepository.pause()
     }

    fun isPlaying(): Boolean{
        return songRepository.isPlaying()
    }
}