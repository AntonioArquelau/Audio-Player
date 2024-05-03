package com.example.audioplayer.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.viewmodel.SongViewModel

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: SongViewModel by lazy {
        SongViewModel()
    }

    private val songAdapter: SongAdapter by lazy {
        SongAdapter(viewModel.getSongList())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSongsList()
    }

    private fun setupSongsList(){
        binding.recycleView.layoutManager = LinearLayoutManager(this)
        binding.recycleView.setHasFixedSize(true)
        binding.recycleView.adapter = songAdapter
    }
}