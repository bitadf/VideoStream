package com.example.videostream

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.videostream.data.local.DataBase
import com.example.videostream.data.local.UserDataBase
import com.example.videostream.data.local.UserDataBaseDao
import com.example.videostream.data.repository.UserDataBaseRepository
import com.example.videostream.databinding.ActivityMainBinding
import com.example.videostream.presentation.fragments.HomeFragment
import com.example.videostream.presentation.fragments.VideoPlayerFragment
import com.example.videostream.viewmodel.RoomUserViewModel
import com.example.videostream.viewmodel.UserDatabaseViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var userViewModel: RoomUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("appPref" , Context.MODE_PRIVATE)
        sharedPref.edit().putInt("current_user" , 1).apply()


        //
        val dataBase = DataBase.getDatabase(this)
        val userDao = dataBase.userDao()
        val userRep = UserDataBaseRepository(userDao)

        userViewModel = ViewModelProvider(this ,
            UserDatabaseViewModelFactory(userRep)).get(RoomUserViewModel::class.java)

        lifecycleScope.launch {
            userViewModel.addUser(UserDataBase(1, R.string.first_user.toString()))
            userViewModel.addUser(UserDataBase(2, R.string.second_user.toString()))
        }
        supportFragmentManager.beginTransaction()
            .replace( R.id.main_frame , HomeFragment())
            .commit()

    }
}