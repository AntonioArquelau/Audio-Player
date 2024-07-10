package com.example.audioplayer.repositoryimpl
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.audioplayer.model.Song
import com.example.audioplayer.repository.SongRepositoryInterface
import com.example.audioplayer.service.PlayService
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor (
    private val context: Context,
): SongRepositoryInterface, ServiceConnection {

    private val songsList = mutableListOf<Song>()
    private var playService: PlayService? = null
    private var position = 0
    private var onCreateListener: (() -> Unit?)? = null
    @SuppressLint("Range")
    override fun getSongsList(): List<Song> {
        songsList.clear()
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

    override fun createService(position: Int, intent: Intent){
        if (playService == null) {
            val serviceIntent = Intent(context, PlayService::class.java)
            context.bindService(serviceIntent, this, BIND_AUTO_CREATE)
            context.startService(serviceIntent)
        }
        this.position = position
        playService?.play(this.position)
    }

    override fun play() {
        playService?.mediaPlayer?.play()
    }

    override fun pause() {
        playService?.mediaPlayer?.pause()
    }

    override fun random(enabled: Boolean) {
       playService?.random(enabled)
    }

    override fun repeat(): Boolean {
        return playService?.repeat() == true
    }

    override fun next() {
        playService?.next()
    }

    override fun previous() {
        playService?.previous()
    }

    override fun isPlaying(): Boolean {
        return playService?.mediaPlayer?.isPlaying == true
    }

    override fun getMediaPlayer(): ExoPlayer? {
        return playService?.mediaPlayer
    }

    override fun setCreateServiceListener(listener: () -> Unit) {
        onCreateListener = listener
    }

    override fun destroyService() {
        playService?.destroyService()
    }

    override fun getOnSongChangeLivedata(): MutableLiveData<Song>? {
        return playService?.songUpdatedLiveData
    }

    override fun getCurrentSongId(): Int? {
        return playService?.mediaPlayer?.currentMediaItemIndex
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as PlayService.MyBinder
        playService = binder.service
        playService?.create(songsList, position)
        onCreateListener?.invoke()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
    }
}


