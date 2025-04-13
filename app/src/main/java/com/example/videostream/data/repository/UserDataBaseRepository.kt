package com.example.videostream.data.repository

import androidx.lifecycle.LiveData
import com.example.videostream.data.local.UserDataBase
import com.example.videostream.data.local.UserDataBaseDao

class UserDataBaseRepository(private val userDao : UserDataBaseDao) {

    fun getUsers() : LiveData<List<UserDataBase>> = userDao.getUsers()

    suspend fun addUser(user : UserDataBase){
        userDao.insert(user)
    }
}