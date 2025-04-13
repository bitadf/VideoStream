package com.example.videostream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.videostream.data.repository.VideoDataBaseRepository

class VideoDatabaseViewModelFactory(
    private val rep : VideoDataBaseRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RoomVideoViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return RoomVideoViewModel(rep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}