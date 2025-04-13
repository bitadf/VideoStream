package com.example.videostream.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.videostream.data.dataclasses.Video

@Dao
interface VideoDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video : VideoDataBase)
    @Query("SELECT COUNT(*) FROM VideoDataBase WHERE videoId = :videoId AND userId = :userId")
    suspend fun videoExists(videoId: Int, userId: Int): Int

    @Query("SELECT * FROM VideoDataBase WHERE userId = :userId")
    fun getVideos(userId : Int):LiveData<List<VideoDataBase>>

    @Query("DELETE FROM VideoDataBase WHERE videoId = :videoId AND userId = :userId")
    suspend fun deleteVideo(videoId : Int , userId: Int)

}