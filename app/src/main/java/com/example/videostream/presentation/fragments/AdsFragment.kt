package com.example.videostream.presentation.fragments

import VideoMapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.DataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import com.example.videostream.databinding.FragmentAdsBinding
import com.example.videostream.utils.Duration
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.RoomVideoViewModel
import com.example.videostream.viewmodel.SharedViewModel
import com.example.videostream.viewmodel.VideoDatabaseViewModelFactory
import com.example.videostream.viewmodel.VideoViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdsFragment : Fragment() {

    private lateinit var binding: FragmentAdsBinding

    private lateinit var viewModel: VideoViewModel
    private lateinit var roomVideoViewModel: RoomVideoViewModel
    private lateinit var sharedPref: SharedViewModel

    private var adId: Int = 0

    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var adPlayer: ExoPlayer

    private var adTimerJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        adId = arguments?.getInt("ad") ?: 0
        binding = FragmentAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataBase = DataBase.getDatabase(requireActivity())
        val videoDao = dataBase.videoDao()
        val videoRep = VideoDataBaseRepository(videoDao, VideoMapper())

        roomVideoViewModel = ViewModelProvider(
            requireActivity(), VideoDatabaseViewModelFactory(videoRep)
        ).get(RoomVideoViewModel::class.java)

        sharedPref = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(VideoViewModel::class.java)

        if (adId == 0) viewModel.getFirstAd()
        else if (adId == 1) viewModel.getSecondAd()
        else viewModel.getThirdAd()

        viewModel.ad.observe(viewLifecycleOwner) {
            playAd(it)
        }

        backPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                stopAdAndGoToVideoPlayer()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    private fun updateUISecondsLeft(time: Int) {
        val controllerView = binding.videoAdPlayer.findViewById<View>(R.id.ad_controller)
        val layout = controllerView.findViewById<LinearLayout>(R.id.skip_ad_layout)
        val icon = controllerView.findViewById<View>(R.id.skip_ad_icon)
        val remainedTime = controllerView.findViewById<TextView>(R.id.skip_ad_remained_time)
        val dots = controllerView.findViewById<TextView>(R.id.skip_ad_dots)
        val sec = controllerView.findViewById<TextView>(R.id.skip_ad_seconds_text)

        remainedTime.text = time.toString()
        layout.visibility = View.VISIBLE
        if (time > 0) {
            icon.visibility = View.GONE
            remainedTime.visibility = View.VISIBLE
            dots.visibility = View.VISIBLE
            sec.visibility = View.VISIBLE
        } else {
            icon.visibility = View.VISIBLE
            remainedTime.visibility = View.GONE
            dots.visibility = View.GONE
            sec.visibility = View.GONE
        }
    }

    private fun playAd(ad: Video) {
        adPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.videoAdPlayer.player = adPlayer

        val mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(ad.url)
        adPlayer.setMediaItem(mediaItem)
        adPlayer.prepare()
        adPlayer.playWhenReady = true

        val controllerView = binding.videoAdPlayer.findViewById<View>(R.id.ad_controller)
        val title = controllerView.findViewById<TextView>(R.id.ad_title)
        val duration = controllerView.findViewById<TextView>(R.id.ad_duration)
        val seekBar = controllerView.findViewById<LinearProgressIndicator>(R.id.ad_seekbar)
        val passedTime = controllerView.findViewById<TextView>(R.id.ad_passed_time)
        val skipLayout = controllerView.findViewById<LinearLayout>(R.id.skip_ad_layout)

        title.text = ad.title
        duration.text = Duration.formatDurationToVideoTime(ad.duration)
        seekBar.max = (ad.duration * 1000).toInt()
        seekBar.progress = 0
        passedTime.text = "00:00"

        var count = 4

        adTimerJob = lifecycleScope.launch {
            while (true) {
                val current = adPlayer.currentPosition
                passedTime.text = Duration.formatDurationToVideoTime(current / 1000.0)
                seekBar.progress = current.toInt()
                delay(500)

                val timeLeft = 15000 - current
                if (timeLeft <= 4000) {
                    updateUISecondsLeft(count--)
                }

                if (current >= 15000) {
                    backPressedCallback.isEnabled = true
                    skipLayout.setOnClickListener {
//                        adTimerJob?.cancel()
//                        adPlayer.stop()
//                        adPlayer.release()
//                        stopAdAndGoToVideoPlayer()
//                        requireActivity().supportFragmentManager
//                            .beginTransaction()
//                            .remove(requireActivity().supportFragmentManager.findFragmentByTag("ad")!!)
//                            .commit()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    break
                }
            }
        }
    }

    private fun stopAdAndGoToVideoPlayer() {
        adTimerJob?.cancel()
        adPlayer.stop()
        adPlayer.release()
        backPressedCallback.isEnabled = false

        //parentFragmentManager.popBackStack()
       // requireActivity().supportFragmentManager.popBackStack()


//        lifecycleScope.launchWhenResumed {
//            FragmentChanging.change(
//                parentFragmentManager, VideoPlayerFragment()
//            )
//        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        adTimerJob?.cancel()
//        if (this::adPlayer.isInitialized) adPlayer.release()
//    }
}
