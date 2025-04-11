package com.example.videostream.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.repository.VideoRepository

import com.example.videostream.utils.Constants.VIDEO_API
import kotlinx.coroutines.launch

class VideoViewModel : ViewModel() {

    private val videoRep = VideoRepository()

    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos : LiveData<List<Video>> = _videos

    fun getVideos(offset : Int){
        viewModelScope.launch {
            val videoResponse = videoRep.getVideos(offset)
            if(videoResponse.isSuccessful && videoResponse.body()?.done == true){
                _videos.postValue(videoResponse.body()!!.result.videos)
            }
            else{
                    Log.d(VIDEO_API , "Error: done=false or unsuccessful")

            }
        }
    }
}