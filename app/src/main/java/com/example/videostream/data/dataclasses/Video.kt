package com.example.videostream.data.dataclasses

import android.os.Parcelable

import java.io.Serializable

data class Video (
    val id : Int ,
    val title : String ,
    val url : String ,
    val duration : Double ,

)