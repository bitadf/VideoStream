package com.example.videostream.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.videostream.R
import com.example.videostream.data.dataclasses.VideoImage

class VideoAdapter (
    private val context: Context ,
    private val videoList : List<VideoImage>
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>(){

    class ViewHolder(view : View): RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_card , parent , false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = videoList[position]

    }

}