package com.example.videostream.presentation.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.databinding.FragmentHomeBinding
import com.example.videostream.presentation.adapters.VideoAdapter
import com.example.videostream.utils.Constants.PLAY_VIDEO
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.VideoViewModel


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    //recycler
    private lateinit var videoRecycler : RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private var videoList : MutableList<Video> = mutableListOf()

    //view models
    private lateinit var videoViewModel : VideoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialize
        videoViewModel = ViewModelProvider(requireActivity()).get(VideoViewModel::class.java)

        //set recycler
        videoRecycler = binding.homeVideoRecycler
        videoRecycler.layoutManager = LinearLayoutManager(requireContext())

        videoAdapter = VideoAdapter(requireContext() , videoList) { _, video ->

            FragmentChanging.passVideoToFragment(
                parentFragmentManager,
                video,

            )

        }
        videoRecycler.adapter = videoAdapter

        //load videos
        videoViewModel.videos.observe(viewLifecycleOwner){videos ->
            videoList.clear()
            videoList.addAll(videos)
            videoAdapter.notifyDataSetChanged()

        }
        videoViewModel.getVideos(5)



        //change user
        binding.mainUserSelectLayout.setOnClickListener{
            val users = UsersFragment()
            users.show(parentFragmentManager, "UserBottomSheet" )
        }
    }


}