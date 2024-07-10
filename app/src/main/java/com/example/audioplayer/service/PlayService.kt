package com.example.audioplayer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.os.IBinder
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.MediaDescriptionAdapter
import androidx.media3.ui.PlayerNotificationManager.NotificationListener
import com.example.audioplayer.R
import com.example.audioplayer.extra.IntentString.Companion.INTENT_SONG_POSITION
import com.example.audioplayer.model.Song
import com.example.audioplayer.view.MainActivity

class PlayService: Service() {

    private val mBinder = MyBinder()
    val mediaPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(applicationContext).build().apply { playWhenReady = true }
    }
    private var songsList = mutableListOf<Song>()
    private var currentPosition = 0
    var channelId = ""
    val notificationId = 1111111
    var enableRandom = false

    var notificationManager: PlayerNotificationManager? = null
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
        createNotification()
        return super.onStartCommand(intent, flags, startId)
    }


    fun createNotification(){
        channelId = resources.getString(R.string.app_name) + " Music Channel "

        notificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setChannelImportance(NotificationUtil.IMPORTANCE_HIGH)
            .setSmallIconResourceId(R.drawable.baseline_play_arrow_24)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setPreviousActionIconResourceId(R.drawable.baseline_fast_rewind_24)
            .setPauseActionIconResourceId(R.drawable.baseline_pause_24)
            .setPlayActionIconResourceId(R.drawable.baseline_play_arrow_24)
            .setNextActionIconResourceId(R.drawable.baseline_play_arrow_24)
            .setChannelNameResourceId(R.string.app_name)
            .setMediaDescriptionAdapter(object : MediaDescriptionAdapter{
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return songsList[currentPosition].info.toString()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val openAppIntent = Intent(applicationContext, MainActivity::class.java)
                    return PendingIntent.getActivity(applicationContext, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return songsList[currentPosition].name
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    val imageView: ImageView = ImageView(applicationContext)
                    //imageView.setImageURI(Objects.requireNonNull(mediaPlayer.currentMediaItem?.mediaMetadata?.artworkUri))
                    var bitmap =  imageView.drawable as BitmapDrawable?
//                    if(bitmap == null)
//                        bitmap = ContextCompat.getDrawable(applicationContext, R.drawable.ic_launcher_background) as BitmapDrawable

                    return ContextCompat.getDrawable(applicationContext, R.drawable.ic_launcher_background)?.toBitmap()
                }

            })
            .setNotificationListener(object : NotificationListener{
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    if(mediaPlayer.isPlaying)
                        mediaPlayer.pause()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    super.onNotificationPosted(notificationId, notification, ongoing)
                    startForeground(notificationId, notification)
                }
            })
            .build()
        notificationManager?.setPlayer(mediaPlayer)
        notificationManager?.setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager?.setUseStopAction(false)
        notificationManager?.setUseRewindAction(true)
        notificationManager?.setUseFastForwardAction(true)
        notificationManager?.setUsePreviousAction(true)
        notificationManager?.setUseNextAction(true)
        notificationManager?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

    }

    fun destroyService() {
        if(mediaPlayer.isPlaying)
            mediaPlayer.stop()
        notificationManager?.setPlayer(null)
        stopForeground(notificationId)
        super.onDestroy()
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
        stopForeground(notificationId)
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
        notificationManager?.setPlayer(mediaPlayer)
        val uri = position.let { songsList[it].path?.toUri() }
        val mediaItem = uri?.let { MediaItem.fromUri(it) }
        mediaItem?.let { mediaPlayer.setMediaItem(it) }
        mediaPlayer.prepare()
        currentPosition = position
        play()
    }

}
