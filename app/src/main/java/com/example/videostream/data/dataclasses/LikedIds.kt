package com.example.videostream.data.dataclasses

data class LikedIds(
    val userId : Int ,
    val videoId : MutableList<Int> = mutableListOf()
)
