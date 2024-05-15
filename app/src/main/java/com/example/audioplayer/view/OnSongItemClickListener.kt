package com.example.audioplayer.view

import com.example.audioplayer.model.Song

interface OnSongItemClickListener {
    fun onItemClick(song: Song, id: Int)
}