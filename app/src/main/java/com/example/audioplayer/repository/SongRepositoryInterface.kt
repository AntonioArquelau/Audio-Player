package com.example.audioplayer.repository

import android.content.Intent
import android.media.MediaPlayer
import androidx.media3.exoplayer.ExoPlayer
import com.example.audioplayer.model.Song

interface SongRepositoryInterface {
    fun getSongsList(): List<Song>
    fun createService(position: Int, intent: Intent)
    fun play()
    fun pause()
    fun random(enabled: Boolean)
    fun repeat(): Boolean
    fun next(): Song
    fun previous(): Song
    fun isPlaying(): Boolean
    fun getMediaPlayer(): ExoPlayer?
    fun setCreateServiceListener(listener: () -> Unit)
}