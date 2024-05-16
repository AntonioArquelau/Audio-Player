package com.example.audioplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import com.example.audioplayer.extra.IntentString.Companion.INTENT_SONG_POSITION
import com.example.audioplayer.model.Song

class PlayService: Service() {

    private val mBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private var songsList = mutableListOf<Song>()
    private var currentPosition = 0
    var enableRandom = false
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class MyBinder() : Binder() {
        val service: PlayService
            get() = this@PlayService;
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val position = intent?.getIntExtra(INTENT_SONG_POSITION, 0)
        return super.onStartCommand(intent, flags, startId)
    }

    fun create(songs: List<Song>, position: Int){
        songsList = songs.toMutableList()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        val uri = position.let { songsList[it].path?.toUri() }
        if( mediaPlayer == null) mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(uri.toString())
        mediaPlayer?.prepare()
        play()
    }

    fun play() {
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            if(enableRandom){
                currentPosition = (Math.random() * (songsList.size - 1)).toInt()
                play(currentPosition)
            }
            else {
                play(currentPosition + 1)
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun random(enabled: Boolean) {
        enableRandom = enabled
    }

    fun repeat(): Boolean {
        mediaPlayer?.isLooping = !mediaPlayer?.isLooping!!
        return mediaPlayer?.isLooping == true
    }

    fun play(position: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        val uri = position.let { songsList[it].path?.toUri() }
        mediaPlayer?.setDataSource(uri.toString())
        mediaPlayer?.prepare()
        currentPosition = position
        play()
    }

}
