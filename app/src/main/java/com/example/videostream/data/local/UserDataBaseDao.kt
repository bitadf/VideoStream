package com.example.videostream.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user : UserDataBase)

    @Query("SELECT * FROM userdatabase")
    fun getUsers():LiveData<List<UserDataBase>>

}