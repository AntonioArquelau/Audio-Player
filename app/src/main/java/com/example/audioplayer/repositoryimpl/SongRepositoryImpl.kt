package com.example.audioplayer.repositoryimpl

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.contentValuesOf
import com.example.audioplayer.model.Song
import com.example.audioplayer.repository.SongRepositoryInterface
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor (
    private val context: Context
): SongRepositoryInterface {
    @SuppressLint("Range")
    override fun getSongsList(): List<Song> {
        val songsList = mutableListOf<Song>()
        val allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC+"!=0"
        val cursor = context.contentResolver.query(allSongsUri, null, selection, null, null)
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    val songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    val songDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val song = Song(songName, songAuthor, songDuration, songURL)
                    songsList.add(song)
                }while (cursor.moveToNext())
            }
        }
        return songsList
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

    override fun next() {
        TODO("Not yet implemented")
    }

    override fun previous() {
        TODO("Not yet implemented")
    }
}