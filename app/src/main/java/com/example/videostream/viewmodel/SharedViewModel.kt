package com.example.videostream.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _currentUserId = MutableLiveData<Int>()
    val currentUserId: LiveData<Int> = _currentUserId

    private val _videoCount = MutableLiveData<Int>(0)
    val videoCount : LiveData<Int> = _videoCount

    fun addVideoCount(){
        _videoCount.postValue((_videoCount.value ?: 0) + 1)
    }
    fun refreshVideoCount(){
        _videoCount.postValue(0)
    }

    fun setCurrentUser(userId: Int) {
        _currentUserId.value = userId
    }
    fun getCurrentUser () : Int{
        return _currentUserId.value?.toInt() ?: 1
    }

}