package com.example.audioplayer.view

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import com.example.audioplayer.R
import com.example.audioplayer.R.color.light_gray
import com.example.audioplayer.R.color.transparent
import com.example.audioplayer.databinding.ActivityPlayBinding
import com.example.audioplayer.extra.IntentString
import com.example.audioplayer.model.Song
import com.example.audioplayer.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.String
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlayActivity (): AppCompatActivity() {

    companion object{
        const val FORMATTER = "%02d:%02d"
    }

    private val binding: ActivityPlayBinding by lazy {
        ActivityPlayBinding.inflate(layoutInflater)
    }

    private val viewModel: SongViewModel by viewModels()
    private var isSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val songName = intent.getStringExtra(IntentString.INTENT_SONG_NAME)
        val songInfo = intent.getStringExtra(IntentString.INTENT_SONG_INFO)
        val songDuration = intent.getStringExtra(IntentString.INTENT_SONG_DURATION)

        //This will be used to load and play the song on the comimg commits.
        val songPosition = intent.getIntExtra(IntentString.INTENT_SONG_POSITION, 0)
        val song = Song(songName, songInfo, songDuration, null)
        updateUI(song)


        songPosition.let {
            viewModel.createService(it, intent)
        }
        setupToolbar()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_OK)
                finish()
            }
        })

        binding.playPauseButton.setOnClickListener{
            if (viewModel.isPlaying()){
                viewModel.pause()
                binding.playPauseButton.icon = resources.getDrawable(R.drawable.baseline_play_arrow_24, theme)
            }
            else{
                viewModel.play()
                binding.playPauseButton.icon = resources.getDrawable(R.drawable.baseline_pause_24, theme)
            }
        }

        binding.nextButton.setOnClickListener{
           val song = viewModel.next()
            updateUI(song)
        }
        binding.prevButton.setOnClickListener{
           val song = viewModel.prev()
            updateUI(song)
        }

        binding.repeatToggleButton.setOnClickListener{
            binding.repeatToggleButton.isActivated = viewModel.toggleRepeat()
            if (binding.repeatToggleButton.isActivated){
                binding.repeatToggleButton.backgroundTintList = resources.getColorStateList(light_gray, theme)
            }
            else{
                binding.repeatToggleButton.backgroundTintList = resources.getColorStateList(
                    transparent, theme)
            }
        }

        binding.randomToggleButton.setOnClickListener{
            binding.randomToggleButton.isActivated =  !binding.randomToggleButton.isActivated
            viewModel.toggleRandom(binding.randomToggleButton.isActivated)
            if (binding.randomToggleButton.isActivated){
                binding.randomToggleButton.backgroundTintList = resources.getColorStateList(light_gray, theme)
            }
            else{
                binding.randomToggleButton.backgroundTintList = resources.getColorStateList(
                    transparent, theme)
            }
        }

        viewModel.setOnCreateServiceListener {
            viewModel.getMediaPlayer()?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when(playbackState){
                        Player.STATE_ENDED ->{
                            val song = viewModel.next()
                            updateUI(song)
                        }
                    }
                    super.onPlaybackStateChanged(playbackState)
                }
            })
        }


    }

    @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
    private fun updateUI(song: Song){
        binding.songInfoTextview.text = song.info
        binding.songNameTextview.text = song.name

        val songDuration: Long = song.duration?.toLong()!!
        val hms =formatString(songDuration)
        binding.durationTimeTextView.text = hms
        binding.playPauseButton.icon = resources.getDrawable(R.drawable.baseline_pause_24, theme)
        setupSeekBar(songDuration)
    }

    fun formatString(songDuration: Long): kotlin.String{
        return String.format(
            FORMATTER,
            TimeUnit.MILLISECONDS.toMinutes(songDuration) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    songDuration
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(songDuration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    songDuration
                )
            )
        )
    }
    fun setupSeekBar(duration: Long){
        binding.seekbar.max = duration.toInt()
        val mHandler = Handler()
        this.runOnUiThread(object : Runnable {
            override fun run() {
                if(!isSeeking) {
                    if (viewModel.getMediaPlayer() != null) {
                        val mCurrentPosition: Int = viewModel.getMediaPlayer()!!.currentPosition.toInt()
                        binding.seekbar.setProgress(mCurrentPosition)
                        binding.currentTimeTextView.text = formatString(mCurrentPosition.toLong())
                    }
                }
                mHandler.postDelayed(this, 100)

            }
        })

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(isSeeking) {
                    viewModel.getMediaPlayer()?.seekTo(progress.toLong())
                    binding.currentTimeTextView.text = formatString(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
            }

        })
    }

    
    private fun setupToolbar(){
        supportActionBar?.title = "Playing"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
            }
            else ->
            {}
        }
        return super.onOptionsItemSelected(item)
    }
}