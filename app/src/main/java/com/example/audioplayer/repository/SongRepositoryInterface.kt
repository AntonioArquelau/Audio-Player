package com.example.audioplayer.repository

import android.content.Intent
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
}