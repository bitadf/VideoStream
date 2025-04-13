package com.example.videostream.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.videostream.data.repository.UserDataBaseRepository

class UserDatabaseViewModelFactory(
    private val rep : UserDataBaseRepository
)  : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomUserViewModel(rep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}