package com.example.audioplayer.repository

import com.example.audioplayer.model.Song

interface SongRepositoryInterface {
    fun getSongsList(): List<Song>
    fun play()
    fun pause()
    fun random(enable: Boolean)
    fun repeat(enable: Boolean)
    fun next()
    fun previous()
}