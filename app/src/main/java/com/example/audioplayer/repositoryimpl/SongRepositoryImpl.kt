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
import com.example.audioplayer.model.Song
import com.example.audioplayer.repository.SongRepositoryInterface
import com.example.audioplayer.service.PlayService
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor (
    private val context: Context,
): SongRepositoryInterface, ServiceConnection {

    private  val songsList = mutableListOf<Song>()
    private var playService: PlayService? = null
    private var position = 0
    private var onCreateListener: (() -> Unit?)? = null
    @SuppressLint("Range")
    override fun getSongsList(): List<Song> {

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
        val serviceIntent = Intent(context, PlayService::class.java)
        context.bindService(serviceIntent, this, BIND_AUTO_CREATE)
        context.startService(serviceIntent)
        this.position = position
    }

    override fun play() {
        playService?.mediaPlayer?.start()
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

    override fun next(): Song {
        if (position + 1 >= songsList.size)
        {
            position = 0
        }
        else{
            position += 1
        }
        if (playService?.enableRandom == true){
            position = (Math.random() * (songsList.size - 1)).toInt()
        }
        playService?.play(position)

        return songsList[position]
    }

    override fun previous(): Song {
        if (position - 1 < 0)
        {
            position = songsList.size - 1
        }
        else{
            position -= 1
        }
        playService?.play(position)

        return songsList[position]
    }

    override fun isPlaying(): Boolean {
        return playService?.mediaPlayer?.isPlaying == true
    }

    override fun getMediaPlayer(): MediaPlayer? {
        return playService?.mediaPlayer
    }

    override fun setCreateServiceListener(listener: () -> Unit) {
        onCreateListener = listener
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as PlayService.MyBinder
        playService = binder.service
        playService?.create(songsList, position)
        onCreateListener?.invoke()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        playService = null
    }
}


