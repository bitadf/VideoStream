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

    private val _ads = MutableLiveData<Video>()
    val ad : LiveData<Video> = _ads



    fun findNextVideo(id : Int , direction : Int) : Video?{
        //direction == 1 next
        //direction == -1 previews
        val videoList = _videos.value
        if(!videoList.isNullOrEmpty()){
            for(i in videoList.indices){
                if(videoList[i].id == id){

                    if(direction == 1 ){
                        if(i < videoList.size)return videoList[i + 1]
                        else return videoList[0]
                    }

                    if (direction == -1 ){
                        if((i - 1) >= 0 )return videoList[i - 1]
                        else return videoList[videoList.size - 1]
                    }
                }
            }
        }
        return null


    }

    fun getFirstAd() {
        viewModelScope.launch {
            val adsRes = videoRep.getAds()
            if (adsRes.isSuccessful && adsRes.body()?.done == true) {
                val adsList = adsRes.body()?.result?.advertises
                if (!adsList.isNullOrEmpty()) {
                    val ad = adsList[2]
                    _ads.postValue(ad)
                } else {
                    Log.e(VIDEO_API, "Ads list is null or empty")
                }
            } else {
                Log.e(VIDEO_API, "Ads request failed or done=false")
            }
        }
    }
    fun getSecondAd(){
        viewModelScope.launch {
            val adsRes = videoRep.getAds()
            if (adsRes.isSuccessful && adsRes.body()?.done == true) {
                val adsList = adsRes.body()?.result?.advertises
                if (!adsList.isNullOrEmpty()) {
                    val ad = adsList[1]
                    _ads.postValue(ad)
                } else {
                    Log.e(VIDEO_API, "Ads list is null or empty")
                }
            } else {
                Log.e(VIDEO_API, "Ads request failed or done=false")
            }
        }
    }
    fun getThirdAd(){
        viewModelScope.launch {
            val adsRes = videoRep.getAds()
            if (adsRes.isSuccessful && adsRes.body()?.done == true) {
                val adsList = adsRes.body()?.result?.advertises
                if (!adsList.isNullOrEmpty()) {
                    val ad = adsList[0]
                    _ads.postValue(ad)
                } else {
                    Log.e(VIDEO_API, "Ads list is null or empty")
                }
            } else {
                Log.e(VIDEO_API, "Ads request failed or done=false")
            }
        }
    }
    fun getVideos(offset: Int) {
        viewModelScope.launch {
            val videoResponse = videoRep.getVideos(offset)
            if (videoResponse.isSuccessful && videoResponse.body()?.done == true) {
                val newVideos = videoResponse.body()!!.result.videos
                val currentVideos = _videos.value ?: emptyList()
                _videos.postValue(currentVideos + newVideos)
            } else {
                Log.d(VIDEO_API, "Error: done=false or unsuccessful")
            }
        }
    }


}