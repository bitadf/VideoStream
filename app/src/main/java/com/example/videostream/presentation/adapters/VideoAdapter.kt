package com.example.videostream.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.utils.Duration
import com.google.android.material.card.MaterialCardView

class VideoAdapter (
    private val context: Context ,
    private val videoList : List<Video> ,
    private val onItemClick : ((position : Int , item:Video) -> Unit )? =null
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>(){

    class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        val videoImage : AppCompatImageView = view.findViewById(R.id.video_card_image)
        val videoDuration : TextView = view.findViewById(R.id.video_card_duration)
        val videoCard : MaterialCardView = view.findViewById(R.id.video_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_card , parent , false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentVideo = videoList[position]

        //set image
        Glide.with(context)
            .asBitmap()
            .load(currentVideo.url)
            .frame(1000000)
            .into(holder.videoImage)

        holder.videoDuration.text = Duration.formatDurationToVideoTime(currentVideo.duration)

        holder.videoCard.setOnClickListener{
            onItemClick?.invoke(position ,currentVideo)
        }



    }


}