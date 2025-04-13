package com.example.videostream.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostream.data.local.UserDataBase
import com.example.videostream.data.repository.UserDataBaseRepository
import com.example.videostream.data.repository.VideoDataBaseRepository
import kotlinx.coroutines.launch

class RoomUserViewModel(private val userRepository : UserDataBaseRepository) : ViewModel() {

    val users: LiveData<List<UserDataBase>> = userRepository.getUsers()

    suspend fun addUser(user: UserDataBase) {
        viewModelScope.launch {
            userRepository.addUser(user)
        }
    }
}