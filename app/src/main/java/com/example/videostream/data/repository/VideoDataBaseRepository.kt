package com.example.videostream.data.repository

import VideoMapper
import androidx.lifecycle.LiveData
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.VideoDataBase
import com.example.videostream.data.local.VideoDataBaseDao

class VideoDataBaseRepository(
    private val videoDao : VideoDataBaseDao ,
    private val mapper : VideoMapper
) {
    fun getVideos(userId : Int) : LiveData<List<VideoDataBase>>{
        return videoDao.getVideos(userId)
    }
    suspend fun add(apiVideo : Video , userId: Int){
        val dbVideo = mapper.toDatabase(apiVideo , userId)
        videoDao.insert(dbVideo)
    }
    suspend fun delete(videoId : Int , userId: Int){
        videoDao.deleteVideo(videoId , userId)
    }
    suspend fun videoExists(videoId: Int, userId: Int): Boolean {
        return videoDao.videoExists(videoId, userId) > 0
    }
}