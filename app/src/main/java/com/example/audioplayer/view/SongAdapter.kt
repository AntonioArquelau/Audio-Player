package com.example.audioplayer.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.databinding.SongItemViewBinding
import com.example.audioplayer.model.Song

class SongAdapter(private val songList: List<Song>): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemBinding = SongItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song: Song = songList[position]
        holder.bind(song)
    }

    inner class SongViewHolder(private val item: SongItemViewBinding): RecyclerView.ViewHolder(item.root){
        fun bind (song: Song){
            item.songInfoTextview.text = song.info
            item.songNameTextview.text = song.name
            item.songDurationTextview.text = song.duration
        }
    }
}