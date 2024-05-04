package com.example.audioplayer.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.extra.IntentString
import com.example.audioplayer.model.Song
import com.example.audioplayer.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnSongItemClickListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: SongViewModel by viewModels()

    private val songAdapter: SongAdapter by lazy {
        SongAdapter(viewModel.getSongList(), this)
    }

    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //  you will get result here in result.data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Songs List"
        setupSongsList()
    }

    private fun setupSongsList() {
        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = songAdapter
    }

    private fun startPlayActivity(song: Song){
        val intent = Intent(this, PlayActivity::class.java);
        intent.putExtra(IntentString.INTENT_SONG_NAME, song.name)
        intent.putExtra(IntentString.INTENT_SONG_INFO, song.info)
        intent.putExtra(IntentString.INTENT_SONG_DURATION, song.duration)
        intent.putExtra(IntentString.INTENT_SONG_PATH, song.path)
        startForResult.launch(intent)
    }

    override fun onItemClick(song: Song) {
        startPlayActivity(song)
    }
}