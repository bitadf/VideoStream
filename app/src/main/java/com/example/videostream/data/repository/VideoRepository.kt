package com.example.videostream.data.repository

import com.example.videostream.data.remote.VideoApiClient
import com.example.videostream.data.remote.VideoApiService

class VideoRepository {
    private val service = VideoApiClient.retrofit.create(VideoApiService::class.java)

    suspend fun getVideos(offset:Int) = service.getAllVideos(offset)
}