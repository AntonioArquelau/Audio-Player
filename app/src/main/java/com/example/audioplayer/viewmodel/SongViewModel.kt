package com.example.audioplayer.viewmodel

import android.content.Intent
import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
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

    fun next(){
        songRepository.next()
    }

    fun prev(){
        songRepository.previous()
    }

    fun toggleRepeat(): Boolean{
        return songRepository.repeat()
    }

    fun toggleRandom(enabled: Boolean){
        return songRepository.random(enabled)
    }

    fun getMediaPlayer(): ExoPlayer? {
        return songRepository.getMediaPlayer()
    }

    fun getService(): ExoPlayer? {
        return songRepository.getMediaPlayer()
    }

    fun setOnCreateServiceListener(listener: () -> Unit){
        songRepository.setCreateServiceListener(listener)
    }

    fun destroyService() {
        songRepository.destroyService()
    }

    fun getOnSongChangeLivedata(): MutableLiveData<Song>? {
        return songRepository.getOnSongChangeLivedata()
    }

    fun getCurrentSongId(): Int? {
        return songRepository.getCurrentSongId()
    }
}