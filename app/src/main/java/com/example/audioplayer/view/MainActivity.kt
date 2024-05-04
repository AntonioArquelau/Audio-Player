package com.example.audioplayer.view

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    val REQUEST_PERMISSION_CODE = 1
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
        if(checkStoragePermissions()){
            setupSongsList()
        }
        else {
            requestPermissions()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                setupSongsList()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            //Below android 11
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

}