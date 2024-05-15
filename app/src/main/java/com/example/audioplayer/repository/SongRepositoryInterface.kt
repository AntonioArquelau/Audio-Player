package com.example.audioplayer.repository

import android.content.Intent
import com.example.audioplayer.model.Song

interface SongRepositoryInterface {
    fun getSongsList(): List<Song>
    fun createService(position: Int, intent: Intent)
    fun play()
    fun pause()
    fun random(enable: Boolean)
    fun repeat(enable: Boolean)
    fun next()
    fun previous()
    fun isPlaying(): Boolean
}