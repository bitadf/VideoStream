package com.example.videostream.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.presentation.fragments.HomeFragment
import com.example.videostream.presentation.fragments.VideoPlayerFragment
import com.example.videostream.utils.Constants.PLAY_VIDEO_DUR
import com.example.videostream.utils.Constants.PLAY_VIDEO_ID
import com.example.videostream.utils.Constants.PLAY_VIDEO_TITLE
import com.example.videostream.utils.Constants.PLAY_VIDEO_URL

object FragmentChanging {
    fun passVideoToFragment(fragmentManager: FragmentManager, video: Video){
        val fragment = VideoPlayerFragment()
        val bundle = Bundle()

        bundle.putString(PLAY_VIDEO_TITLE , video.title )
        bundle.putString(PLAY_VIDEO_URL , video.url)
        bundle.putInt(PLAY_VIDEO_ID , video.id)
        bundle.putDouble(PLAY_VIDEO_DUR , video.duration)

        fragment.arguments = bundle
        fragmentManager.beginTransaction()
            .replace(R.id.main_frame , fragment)
            .addToBackStack(null)
            .commit()

    }
    fun backHome(fragmentManager: FragmentManager){

        fragmentManager.beginTransaction()
            .replace(R.id.main_frame , HomeFragment())
            .addToBackStack(null)
            .commit()
    }
    fun change(fragmentManager: FragmentManager , fragment: Fragment ){

        fragmentManager.beginTransaction()
            .replace(R.id.main_frame , fragment)
            .addToBackStack(null)
            .commit()
    }
    fun changePassInt(fragmentManager: FragmentManager , fragment: Fragment , value : Int? = null , key:String? = null){
        val bundle = Bundle()
        if(value!= null && key!=null){
        bundle.putInt(key , value)
        fragment.arguments = bundle}
        fragmentManager.beginTransaction()
            .replace(R.id.main_frame , fragment)

            .commit()

    }
}