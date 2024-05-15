package com.example.audioplayer.view

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.audioplayer.R
import com.example.audioplayer.databinding.ActivityPlayBinding
import com.example.audioplayer.extra.IntentString
import com.example.audioplayer.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayActivity (): AppCompatActivity() {

    private val binding: ActivityPlayBinding by lazy {
        ActivityPlayBinding.inflate(layoutInflater)
    }

    private val viewModel: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val songName = intent.getStringExtra(IntentString.INTENT_SONG_NAME)
        val songInfo = intent.getStringExtra(IntentString.INTENT_SONG_INFO)
        val songDuration = intent.getStringExtra(IntentString.INTENT_SONG_DURATION)

        //This will be used to load and play the song on the comimg commits.
        val songPosition = intent.getIntExtra(IntentString.INTENT_SONG_POSITION, 0)

        binding.songInfoTextview.text = songInfo
        binding.songNameTextview.text = songName
        binding.durationTimeTextView.text = songDuration

        binding.playPauseButton.icon = resources.getDrawable(R.drawable.baseline_pause_24, theme)
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