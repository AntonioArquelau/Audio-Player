package com.example.audioplayer.repositoryimpl

import android.content.Intent
import android.util.Log
import com.example.audioplayer.model.Song
import com.example.audioplayer.repository.SongRepositoryInterface

class FakeSongRepositoryImpl: SongRepositoryInterface {

    companion object{
        const val TAG = "FakeSongRepositoryImpl"
    }

    private val songsList = listOf(
        Song("Music 1", "Artist 1", "3:51", ""),
        Song("Music 2", "Artist 2", "3:52", ""),
        Song("Music 3", "Artist 3", "3:53", ""),
        Song("Music 4", "Artist 4", "3:54", ""),
        Song("Music 5", "Artist 5", "3:55", ""),
        Song("Music 6", "Artist 6", "3:56", ""),
        Song("Music 7", "Artist 7", "3:57", ""),
        Song("Music 8", "Artist 8", "3:58", ""),
        Song("Music 9", "Artist 9", "3:59", ""),
        Song("Music 10", "Artist 10", "4:00", ""),
    )
    override fun getSongsList(): List<Song> {
        return songsList
    }

    override fun createService(position: Int, intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun random(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun repeat(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun next(): Song {
        TODO("Not yet implemented")
    }

    override fun previous(): Song {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }
}