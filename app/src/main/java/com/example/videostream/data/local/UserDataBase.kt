package com.example.videostream.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDataBase(
    @PrimaryKey val userId : Int ,
    val name : String
)
