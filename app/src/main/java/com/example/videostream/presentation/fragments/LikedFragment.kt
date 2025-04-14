package com.example.videostream.presentation.fragments

import VideoMapper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.DataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import com.example.videostream.databinding.FragmentLikedBinding
import com.example.videostream.presentation.adapters.VideoAdapter
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.RoomVideoViewModel
import com.example.videostream.viewmodel.SharedViewModel
import com.example.videostream.viewmodel.VideoDatabaseViewModelFactory

class LikedFragment : Fragment() {
    private lateinit var binding: FragmentLikedBinding
    private lateinit var videoAdapter: VideoAdapter

    private lateinit var roomVideoViewModel: RoomVideoViewModel
    private lateinit var sharedPrefViewModel: SharedViewModel

    private var currentUserId = -1
    private var videoList: List<Video> = emptyList()
    private var likedIdsMap: Map<Int, List<Int>> = emptyMap()
    private var videoMapper = VideoMapper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.likedRecycler.layoutManager = LinearLayoutManager(requireContext())

        val database = DataBase.getDatabase(requireActivity())
        val videoDao = database.videoDao()
        val videoRepo = VideoDataBaseRepository(videoDao, videoMapper)

        roomVideoViewModel = ViewModelProvider(
            requireActivity(),
            VideoDatabaseViewModelFactory(videoRepo)
        )[RoomVideoViewModel::class.java]

        sharedPrefViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        videoAdapter = VideoAdapter(
            context = requireContext(),
            videoList = videoList,
            roomVideoViewModel = roomVideoViewModel,
            sharedViewModel = sharedPrefViewModel,
            lifecycleOwner = viewLifecycleOwner
        ) { _, video ->
            FragmentChanging.passVideoToFragment(parentFragmentManager, video)
        }

        binding.likedRecycler.adapter = videoAdapter

        // Observe user ID
        sharedPrefViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            currentUserId = userId
            videoAdapter.setCurrentUserId(currentUserId)
            videoAdapter.updateVideoList(videoList)

            // Now observe videos for the current user
            roomVideoViewModel.getVideos(currentUserId).observe(viewLifecycleOwner) { dbVideos ->
                videoList = dbVideos.map { videoMapper.toApi(it) }
                videoAdapter.updateVideoList(videoList)
            }
        }


        // Observe liked video IDs (independent of user observer)
        roomVideoViewModel.likedIds.observe(viewLifecycleOwner) { likedMap ->
            likedIdsMap = likedMap
            videoAdapter.setLikedIds(likedIdsMap)
        }



        binding.likeBackHome.setOnClickListener {
            FragmentChanging.backHome(parentFragmentManager)
        }
        binding.likedRecycler.isNestedScrollingEnabled = false
    }
}
