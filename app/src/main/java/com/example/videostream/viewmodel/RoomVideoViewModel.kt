package com.example.videostream.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostream.data.dataclasses.LikedIds
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.VideoDataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomVideoViewModel(private val videoRepository : VideoDataBaseRepository) : ViewModel() {

    private val _likedIds = MutableLiveData<Map<Int, List<Int>>>(mapOf())  // userId -> list of liked videoIds
    val likedIds: LiveData<Map<Int, List<Int>>> = _likedIds

    init {
        // Initialize empty for users if needed
        _likedIds.value = mapOf(1 to emptyList(), 2 to emptyList())
    }

    fun getVideos(userId: Int):LiveData<List<VideoDataBase>>{
        return videoRepository.getVideos(userId)
    }
    fun isVideoLiked(userId: Int, videoId: Int): Boolean {
        return _likedIds.value?.get(userId)?.contains(videoId) == true
    }

    fun addVideo(video: Video, userId: Int) {
        viewModelScope.launch {
            videoRepository.add(video, userId)
            val currentMap = _likedIds.value.orEmpty().toMutableMap()
            val currentList = currentMap[userId]?.toMutableList() ?: mutableListOf()
            currentList.add(video.id)
            currentMap[userId] = currentList
            _likedIds.postValue(currentMap)
        }
    }

    fun deleteVideo(videoId: Int, userId: Int) {
        viewModelScope.launch {
            videoRepository.delete(videoId, userId)
            val currentMap = _likedIds.value.orEmpty().toMutableMap()
            val currentList = currentMap[userId]?.toMutableList() ?: mutableListOf()
            currentList.remove(videoId)
            currentMap[userId] = currentList
            _likedIds.postValue(currentMap)
        }
    }
}
