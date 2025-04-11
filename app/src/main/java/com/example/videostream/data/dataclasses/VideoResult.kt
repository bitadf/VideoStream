package com.example.videostream.data.dataclasses

data class VideoResult(
    val videos : List<Video> ,
    val remaining : Int ,
    val end : Boolean

)
