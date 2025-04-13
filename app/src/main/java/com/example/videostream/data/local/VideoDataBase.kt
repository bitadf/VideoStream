package com.example.videostream.data.local

import android.content.Intent
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = UserDataBase::class ,
        parentColumns = ["userId"] ,
        childColumns = ["userId"] ,
    )]
)
data class VideoDataBase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId : Int  ,
    val title : String ,
    val url : String ,
    val duration : Double ,
    val userId: Int ,
)