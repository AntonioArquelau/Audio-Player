package com.example.audioplayer.repositoryimpl
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
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

    override fun isPlaying(): Boolean {
        return playService?.mediaPlayer?.isPlaying == true
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as PlayService.MyBinder
        playService = binder.service
        playService?.create(songsList, position)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        playService = null
    }
}


