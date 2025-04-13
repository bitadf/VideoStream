package com.example.videostream.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _currentUserId = MutableLiveData<Int>()
    val currentUserId: LiveData<Int> = _currentUserId

    fun setCurrentUser(userId: Int) {
        _currentUserId.value = userId
    }
    fun getCurrentUser () : Int{
        return _currentUserId.value?.toInt() ?: 1
    }

}