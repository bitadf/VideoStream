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
    private lateinit var videoRecycler: RecyclerView

    private lateinit var roomVideoViewModel: RoomVideoViewModel
    private lateinit var sharedPrefViewModel: SharedViewModel
    private lateinit var videoList: Video
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLikedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoRecycler = binding.likedRecycler
        videoRecycler.layoutManager = LinearLayoutManager(requireContext())

        val dataBase = DataBase.getDatabase(requireActivity())
        val videoDao = dataBase.videoDao()
        val videoRep = VideoDataBaseRepository(videoDao, VideoMapper())

        roomVideoViewModel = ViewModelProvider(
            requireActivity(),
            VideoDatabaseViewModelFactory(videoRep)
        ).get(RoomVideoViewModel::class.java)

        sharedPrefViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val videoMapper = VideoMapper()

        // observe user id changes
        sharedPrefViewModel.currentUserId.observe(viewLifecycleOwner) { currentUser ->
            // observe likedIds when user changes
            roomVideoViewModel.likedIds.observe(viewLifecycleOwner) { likedIds ->
                if (::videoAdapter.isInitialized) {
                    videoAdapter.setLikedIds(likedIds)
                }
            }

            // fetch videos for that user
            roomVideoViewModel.getVideos(currentUser).observe(viewLifecycleOwner) { dbVideos ->
                val vList = dbVideos.map { videoMapper.toApi(it) }

                // create adapter if not yet initialized
                if (!::videoAdapter.isInitialized) {
                    videoAdapter = VideoAdapter(
                        context = requireContext(),
                        videoList = vList,
                        roomVideoViewModel = roomVideoViewModel,
                        sharedViewModel = sharedPrefViewModel,
                        lifecycleOwner = this
                    ) { _, video ->
                        FragmentChanging.passVideoToFragment(parentFragmentManager, video)
                    }

                    videoRecycler.adapter = videoAdapter
                } else {
                    // if adapter exists, just update the list
                    videoAdapter.updateVideoList(vList)
                }

                // set current user id on adapter
                videoAdapter.setCurrentUserId(currentUser)
            }
        }

        binding.likeBackHome.setOnClickListener {
            FragmentChanging.backHome(parentFragmentManager)
        }
    }


}



