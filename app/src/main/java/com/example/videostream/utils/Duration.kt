package com.example.videostream.utils

object Duration {
    fun formatDurationToVideoTime(duration : Double) : String{
        val totalSeconds = duration.toInt()
        val hour = totalSeconds/ 3600
        val min = (totalSeconds % 3600)/60
        val sec = (totalSeconds % 60)
        val res : String
        if(hour > 0)res = String.format("%02d:%02d:%02d" , hour , min ,sec)
        else res = String.format("%02d:%02d" , min ,sec)
        return res
    }
}