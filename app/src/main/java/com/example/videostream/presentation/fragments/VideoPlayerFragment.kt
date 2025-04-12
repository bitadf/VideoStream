package com.example.videostream.presentation.fragments

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.databinding.FragmentVideoPlayerBinding
import com.example.videostream.utils.Constants.PLAY_VIDEO
import com.example.videostream.utils.Constants.PLAY_VIDEO_DUR
import com.example.videostream.utils.Constants.PLAY_VIDEO_ID
import com.example.videostream.utils.Constants.PLAY_VIDEO_TITLE
import com.example.videostream.utils.Constants.PLAY_VIDEO_URL
import com.example.videostream.utils.Duration
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.VideoViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerFragment : Fragment() {
    private lateinit var binding : FragmentVideoPlayerBinding

    private lateinit var video : ExoPlayer

    private var videoObject : Video? = null

    private lateinit var viewModel: VideoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val title = arguments?.getString(PLAY_VIDEO_TITLE)
        val url = arguments?.getString(PLAY_VIDEO_URL)
        val id = arguments?.getInt(PLAY_VIDEO_ID)
        val dur = arguments?.getDouble(PLAY_VIDEO_DUR)
        if(title != null && url != null && id != null && dur != null){
            videoObject = Video(id , title , url , dur)
        }
        binding = FragmentVideoPlayerBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(VideoViewModel::class.java)
        //player initialization


        if(videoObject != null){
            video = ExoPlayer.Builder(requireContext()).build()
            binding.videoPlayerVideo.player = video
            val videoInstance = com.google.android.exoplayer2.MediaItem.fromUri(videoObject?.url.toString())
            video.setMediaItem(videoInstance)
            video.prepare()
            video.playWhenReady = true


            //////keys
            val pause = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_pause)
            val forward = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_forward)
            val backward = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_backward)
            val duration = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_duration)
            val seekBar = binding.videoPlayerVideo.findViewById<LinearProgressIndicator>(R.id.video_seekbar)
            val passedTime = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_current_time)
            val title = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_title)
            val back = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_back_home)
            //forward
            forward.setOnClickListener{
                val nextVideo = viewModel.findNextVideo(videoObject!!.id , 1)
                if (nextVideo != null) {
                    playVideo(nextVideo)
                }

            }
            backward.setOnClickListener{
                val previousVideo = viewModel.findNextVideo(videoObject!!.id , -1)
                if(previousVideo != null){
                    playVideo(previousVideo)
                }
            }


            pause.setOnClickListener{
                playPauseHandling(pause)
            }

            title.text = videoObject!!.title
            duration.text = Duration.formatDurationToVideoTime(videoObject!!.duration)
            ////passed time
            seekBar.max = videoObject!!.duration.toInt()

            lifecycleScope.launch {
                while(true){
                    val current = video.currentPosition
                    passedTime.text = Duration.formatDurationToVideoTime(current / 1000.0)
                    seekBar.progress = current.toInt()
                    delay(1000)
                }
            }

            back.setOnClickListener {

                    FragmentChanging.backHome(parentFragmentManager)

            }
        }









            //onBackPressed
        val callBack = requireActivity().onBackPressedDispatcher.addCallback(this){
            FragmentChanging.backHome(parentFragmentManager)
        }


    }

    override fun onPause() {
        super.onPause()
        video.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        video.release()
    }

    fun playPauseHandling(icon : AppCompatImageView){
        if(video.isPlaying){
            video.pause()
            icon.setImageResource(R.drawable.pause_icon)
        }
        else{
            video.play()
            icon.setImageResource(R.drawable.play_icon)
        }
    }
    fun playVideo(videoObj : Video){
      videoObject = videoObj
      val mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(videoObj.url)
      video.setMediaItem(mediaItem)
      video.prepare()
      video.playWhenReady = true


        // Update UI
        val title = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_title)
        val duration = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_duration)
        val seekBar = binding.videoPlayerVideo.findViewById<LinearProgressIndicator>(R.id.video_seekbar)
        val passedTime = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_current_time)

        title.text = videoObj.title
        duration.text = Duration.formatDurationToVideoTime(videoObj.duration)
        seekBar.max = videoObj.duration.toInt()
        seekBar.progress = 0
        passedTime.text = "00:00"

    }

}