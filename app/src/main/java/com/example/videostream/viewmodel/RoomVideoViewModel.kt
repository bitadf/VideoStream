package com.example.videostream.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostream.data.dataclasses.LikedIds
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.VideoDataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import kotlinx.coroutines.launch

class RoomVideoViewModel(private val videoRepository : VideoDataBaseRepository) : ViewModel() {

//    private val _likedIds = MutableLiveData<List<LikedIds>>(emptyList())
//    val likedIds : LiveData<List<LikedIds>> = _likedIds

    fun getVideos(userId: Int): LiveData<List<VideoDataBase>> {
        return videoRepository.getVideos(userId)
    }

    suspend fun addVideo(video: Video, userId: Int) {
        viewModelScope.launch {
            videoRepository.add(video, userId)

        }
    }

    fun deleteVideo(videoId: Int, userId: Int) {
        viewModelScope.launch {
            videoRepository.delete(videoId, userId)

        }
    }
}