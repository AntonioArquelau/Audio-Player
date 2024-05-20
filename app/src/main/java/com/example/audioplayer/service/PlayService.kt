package com.example.audioplayer.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.audioplayer.extra.IntentString.Companion.INTENT_SONG_POSITION
import com.example.audioplayer.model.Song

class PlayService: Service() {

    private val mBinder = MyBinder()
    val mediaPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(applicationContext).build().apply { playWhenReady = true }
    }
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
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setAudioAttributes(audioAttributes, true)
        return super.onStartCommand(intent, flags, startId)
    }

    fun create(songs: List<Song>, position: Int){
        songsList = songs.toMutableList()
        val uri = position.let { songsList[it].path?.toUri() }
        val mediaItem = uri?.let { MediaItem.fromUri(it) }
        mediaItem?.let { mediaPlayer.setMediaItem(it) }
        mediaPlayer.prepare()
        play()
    }

    fun play() {
        mediaPlayer.play()
        mediaPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when(playbackState){
                    Player.STATE_ENDED ->{
                        if(enableRandom){
                            currentPosition = (Math.random() * (songsList.size - 1)).toInt()
                            play(currentPosition)
                        }
                        else {
                            play(currentPosition + 1)
                        }
                    }
                }
                super.onPlaybackStateChanged(playbackState)
            }
        })
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun random(enabled: Boolean) {
        enableRandom = enabled
    }

    fun repeat(): Boolean {
        return if (mediaPlayer.repeatMode == Player.REPEAT_MODE_ALL){
            mediaPlayer.repeatMode = Player.REPEAT_MODE_OFF
            false
        }else{
            mediaPlayer.repeatMode = Player.REPEAT_MODE_ALL
            true
        }
    }

    fun play(position: Int) {
        mediaPlayer.stop()
        val uri = position.let { songsList[it].path?.toUri() }
        val mediaItem = uri?.let { MediaItem.fromUri(it) }
        mediaItem?.let { mediaPlayer.setMediaItem(it) }
        mediaPlayer.prepare()
        currentPosition = position
        play()
    }

}
