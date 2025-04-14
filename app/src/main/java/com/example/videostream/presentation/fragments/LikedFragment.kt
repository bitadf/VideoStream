package com.example.videostream.presentation.fragments

import VideoMapper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.DataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import com.example.videostream.databinding.FragmentLikedBinding
import com.example.videostream.presentation.adapters.VideoAdapter
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.RoomVideoViewModel
import com.example.videostream.viewmodel.SharedViewModel
import com.example.videostream.viewmodel.VideoDatabaseViewModelFactory
import com.example.videostream.viewmodel.VideoViewModel

class LikedFragment : Fragment() {
    private lateinit var binding: FragmentLikedBinding
    private lateinit var videoAdapter: VideoAdapter

    private lateinit var roomVideoViewModel: RoomVideoViewModel
    private lateinit var sharedPrefViewModel: SharedViewModel

    private var likedIdsMap: Map<Int, List<Int>>? = null
    private var videoList: List<Video>? = null
    private var currentUserId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = binding.likedRecycler
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val database = DataBase.getDatabase(requireActivity())
        val videoDao = database.videoDao()
        val videoRepo = VideoDataBaseRepository(videoDao, VideoMapper())

        roomVideoViewModel = ViewModelProvider(
            requireActivity(),
            VideoDatabaseViewModelFactory(videoRepo)
        )[RoomVideoViewModel::class.java]

        sharedPrefViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val videoMapper = VideoMapper()

        sharedPrefViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            currentUserId = userId

            // Step 1: Observe likedIds
            roomVideoViewModel.likedIds.observe(viewLifecycleOwner) { likedIds ->
                likedIdsMap = likedIds
                trySetupAdapter(videoMapper)
            }

            // Step 2: Observe videos
            roomVideoViewModel.getVideos(userId).observe(viewLifecycleOwner) { dbVideos ->
                videoList = dbVideos.map { videoMapper.toApi(it) }
                trySetupAdapter(videoMapper)
            }
        }

        binding.likeBackHome.setOnClickListener {
            FragmentChanging.backHome(parentFragmentManager)
        }
    }

    private fun trySetupAdapter(videoMapper: VideoMapper) {
        val videos = videoList
        val likes = likedIdsMap

        // Wait for both to be non-null
        if (videos != null && likes != null && currentUserId != -1) {
            if (!::videoAdapter.isInitialized) {
                videoAdapter = VideoAdapter(
                    context = requireContext(),
                    videoList = videos,
                    roomVideoViewModel = roomVideoViewModel,
                    sharedViewModel = sharedPrefViewModel,
                    lifecycleOwner = viewLifecycleOwner
                ) { _, video ->
                    FragmentChanging.passVideoToFragment(parentFragmentManager, video)
                }

                videoAdapter.setCurrentUserId(currentUserId)
                videoAdapter.setLikedIds(likes)

                binding.likedRecycler.adapter = videoAdapter
            } else {
                videoAdapter.updateVideoList(videos)
                videoAdapter.setLikedIds(likes)
            }
        }
    }
}
