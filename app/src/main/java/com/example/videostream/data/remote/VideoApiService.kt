package com.example.videostream.data.remote

import com.example.videostream.data.dataclasses.VideoResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoApiService {

    @GET("api/videos/index")
    suspend fun getAllVideos(
        @Query("offset") offset : Int
    ) : Response<VideoResponse>
}