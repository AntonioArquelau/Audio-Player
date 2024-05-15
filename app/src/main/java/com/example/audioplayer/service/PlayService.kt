package com.example.audioplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.net.toUri
import com.example.audioplayer.extra.IntentString.Companion.INTENT_SONG_POSITION
import com.example.audioplayer.model.Song

class PlayService: Service() {

    private val mBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private var songsList = mutableListOf<Song>()
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
        val uri = position.let { songsList[it].path.toUri() }
        if( mediaPlayer == null) mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(uri.toString())
        mediaPlayer?.prepare()
        play()
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun random(enable: Boolean) {
        TODO("Not yet implemented")
    }

    fun repeat(enable: Boolean) {
        TODO("Not yet implemented")
    }

    fun next() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        //val nextPosition = if(songRepository.getListSize() > position) position+1 else return
       // val uri = songRepository.getSong(nextPosition).path.toUri()
       // mediaPlayer = MediaPlayer.create(applicationContext, uri)
        mediaPlayer?.prepare()
        play()
    }

    fun previous() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        //val nextPosition = if(position > 0) position-1 else return
      //  val uri = songRepository.getSong(nextPosition).path.toUri()
      //  mediaPlayer = MediaPlayer.create(applicationContext, uri)
        mediaPlayer?.prepare()
        play()
    }
}
