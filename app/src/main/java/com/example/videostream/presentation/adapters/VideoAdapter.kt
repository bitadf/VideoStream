package com.example.videostream.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.utils.Duration
import com.example.videostream.viewmodel.RoomVideoViewModel
import com.example.videostream.viewmodel.SharedViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class VideoAdapter (
    private val context: Context,
    private var videoList : List<Video>,
    private val roomVideoViewModel: RoomVideoViewModel,
    private val sharedViewModel: SharedViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onItemClick : ((position : Int , item:Video ) -> Unit )? = null

) : RecyclerView.Adapter<VideoAdapter.ViewHolder>(){

    private var currentUserId = 1
    private var likedIds: Map<Int, List<Int>> = emptyMap()

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
        notifyDataSetChanged() // or a diff util or targeted notifyItemChanged
    }

    fun setLikedIds(likedIdsMap: Map<Int, List<Int>>) {
        likedIds = likedIdsMap
        notifyDataSetChanged()
    }


    class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        val videoImage : AppCompatImageView = view.findViewById(R.id.video_card_image)
        val videoDuration : TextView = view.findViewById(R.id.video_card_duration)
        val videoCard : MaterialCardView = view.findViewById(R.id.video_card)
        val likeIcon : AppCompatImageView = view.findViewById(R.id.main_liked_icon)
        val userText : TextView = view.findViewById(R.id.video_card_user_text)
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

        holder.userText.text = if (currentUserId == 1)
            context.getString(R.string.first_user)
        else
            context.getString(R.string.second_user)

        val isLiked = likedIds[currentUserId]?.contains(currentVideo.id) == true
        holder.likeIcon.setImageResource(
            if (isLiked) R.drawable.small_filled_like
            else R.drawable.small_empty_like
        )

        holder.likeIcon.setOnClickListener {
            if (isLiked) {
                roomVideoViewModel.deleteVideo(currentVideo.id, currentUserId)
            } else {
                roomVideoViewModel.addVideo(currentVideo, currentUserId)
            }
        }

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
    fun updateVideoList(newList: List<Video>) {
        videoList = newList
        notifyDataSetChanged()
    }



}