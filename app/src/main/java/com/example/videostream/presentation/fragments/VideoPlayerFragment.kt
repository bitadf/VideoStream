package com.example.videostream.presentation.fragments

import VideoMapper
import android.os.Bundle
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
import com.example.videostream.data.local.DataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import com.example.videostream.databinding.FragmentVideoPlayerBinding

import com.example.videostream.utils.Constants.PLAY_VIDEO_DUR
import com.example.videostream.utils.Constants.PLAY_VIDEO_ID
import com.example.videostream.utils.Constants.PLAY_VIDEO_TITLE
import com.example.videostream.utils.Constants.PLAY_VIDEO_URL
import com.example.videostream.utils.Duration
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerFragment : Fragment() {
    private lateinit var binding : FragmentVideoPlayerBinding

    private lateinit var video : ExoPlayer
    private var videoObject : Video? = null

    private lateinit var viewModel: VideoViewModel
    private lateinit var roomVideoViewModel: RoomVideoViewModel
    private lateinit var sharedPref : SharedViewModel

    private var progressJob: Job? = null

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
        sharedPref = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        if(videoObject != null){
            video = ExoPlayer.Builder(requireContext()).build()
            binding.videoPlayerVideo.player = video

            video.addListener(object  : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        videoEnd()
                    }
                }
            })

            val videoInstance = com.google.android.exoplayer2.MediaItem.fromUri(videoObject!!.url)
            video.setMediaItem(videoInstance)
            video.prepare()
            video.playWhenReady = true

            val pause = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_pause)
            val forward = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_forward)
            val backward = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_backward)
            val duration = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_duration)
            val seekBar = binding.videoPlayerVideo.findViewById<LinearProgressIndicator>(R.id.video_seekbar)
            val passedTime = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_current_time)
            val title = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_title)
            val back = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_back_home)
            val userText = binding.videoPlayerVideo.findViewById<TextView>(R.id.main_user_text)
            val likeIcon = binding.videoPlayerVideo.findViewById<AppCompatImageView>(R.id.video_like_icon)

            forward.setOnClickListener {
                val nextVideo = viewModel.findNextVideo(videoObject!!.id , 1)
                if (nextVideo != null) {
                    playVideo(nextVideo)
                }
            }

            backward.setOnClickListener {
                val previousVideo = viewModel.findNextVideo(videoObject!!.id , -1)
                if(previousVideo != null){
                    playVideo(previousVideo)
                }
            }

            pause.setOnClickListener {
                playPauseHandling(pause)
            }

            title.text = videoObject!!.title
            duration.text = Duration.formatDurationToVideoTime(videoObject!!.duration)
            seekBar.max = (videoObject!!.duration * 1000).toInt()
            seekBar.progress = 0
            passedTime.text = "00:00"

            startProgressUpdater()

            back.setOnClickListener {
                FragmentChanging.backHome(parentFragmentManager)
            }

            val dataBase = DataBase.getDatabase(requireActivity())
            val videoDao = dataBase.videoDao()
            val videoRep = VideoDataBaseRepository(videoDao , VideoMapper())
            roomVideoViewModel = ViewModelProvider(requireActivity(),
                VideoDatabaseViewModelFactory(videoRep)).get(RoomVideoViewModel::class.java)

            roomVideoViewModel.likedIds.observe(viewLifecycleOwner) { ids ->
                if(ids.contains(videoObject!!.id)) {
                    likeIcon.setImageResource(R.drawable.small_filled_like)
                } else {
                    likeIcon.setImageResource(R.drawable.play_video_empty_like)
                }
            }

            sharedPref.currentUserId.observe(viewLifecycleOwner) {
                if(it == 1) userText.text = getString(R.string.first_user)
                else userText.text = getString(R.string.second_user)
            }
        }
        sharedPref.videoCount.observe(viewLifecycleOwner) { count ->
            when (count) {
                4, 8, 12 -> FragmentChanging.changePassInt(
                    parentFragmentManager,
                    AdsFragment(),
                    0,
                    "ad"
                )
                16 -> FragmentChanging.changePassInt(parentFragmentManager, AdsFragment(), 1, "ad")
                20 -> FragmentChanging.changePassInt(parentFragmentManager, AdsFragment(), 2, "ad")
            }
        }


        //onBackPressed
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            FragmentChanging.backHome(parentFragmentManager)
        }
    }

    private fun startProgressUpdater() {
        progressJob?.cancel()
        val seekBar = binding.videoPlayerVideo.findViewById<LinearProgressIndicator>(R.id.video_seekbar)
        val passedTime = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_current_time)

        progressJob = lifecycleScope.launch {
            while (true) {
                val current = video.currentPosition
                passedTime.text = Duration.formatDurationToVideoTime(current / 1000.0)
                seekBar.progress = current.toInt()
                delay(500)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        video.pause()
    }

    override fun onDestroyView() {
        progressJob?.cancel()
        video.release()
        super.onDestroyView()
    }

    fun playPauseHandling(icon : AppCompatImageView){
        if(video.isPlaying){
            video.pause()
            icon.setImageResource(R.drawable.pause_icon)
        } else {
            video.play()
            icon.setImageResource(R.drawable.play_icon)
        }
    }

    fun playVideo(videoObj: Video){
        videoObject = videoObj
        val mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(videoObj.url)
        video.setMediaItem(mediaItem)
        video.prepare()
        video.playWhenReady = true

        val title = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_title)
        val duration = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_duration)
        val seekBar = binding.videoPlayerVideo.findViewById<LinearProgressIndicator>(R.id.video_seekbar)
        val passedTime = binding.videoPlayerVideo.findViewById<TextView>(R.id.video_current_time)

        title.text = videoObj.title
        duration.text = Duration.formatDurationToVideoTime(videoObj.duration)
        seekBar.max = (videoObj.duration * 1000).toInt()
        seekBar.progress = 0
        passedTime.text = "00:00"

        startProgressUpdater()
    }

//    fun videoEnd() {
//        sharedPref.addVideoCount()
//        sharedPref.videoCount.observe(viewLifecycleOwner) { count ->
//
//            when (count) {
//                4, 8, 12 -> FragmentChanging.changePassInt(
//                    parentFragmentManager,
//                    AdsFragment(),
//                    0,
//                    "ad"
//                )
//
//                16 -> FragmentChanging.changePassInt(parentFragmentManager, AdsFragment(), 1, "ad")
//                20 -> FragmentChanging.changePassInt(parentFragmentManager, AdsFragment(), 2, "ad")
//            }
//        }
//    }
fun videoEnd() {
    sharedPref.addVideoCount()
}
}
