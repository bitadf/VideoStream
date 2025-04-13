package com.example.videostream.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room


import androidx.room.RoomDatabase
import com.example.videostream.utils.Constants.DATA_BASE
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [UserDataBase::class , VideoDataBase::class] ,
    version = 3
)
abstract class DataBase : RoomDatabase() {
    abstract fun userDao() : UserDataBaseDao
    abstract fun videoDao() : VideoDataBaseDao

    companion object{
        @Volatile
        private var instance : DataBase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): DataBase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    DATA_BASE

                ).fallbackToDestructiveMigration(true)
                    .build()

                instance = newInstance
                newInstance
            }
        }
    }
}