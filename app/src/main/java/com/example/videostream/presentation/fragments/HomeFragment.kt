package com.example.videostream.presentation.fragments

import VideoMapper
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videostream.R
import com.example.videostream.data.dataclasses.Video
import com.example.videostream.data.local.DataBase
import com.example.videostream.data.repository.VideoDataBaseRepository
import com.example.videostream.databinding.FragmentHomeBinding
import com.example.videostream.presentation.adapters.VideoAdapter
import com.example.videostream.utils.Constants.PLAY_VIDEO
import com.example.videostream.utils.FragmentChanging
import com.example.videostream.viewmodel.RoomVideoViewModel
import com.example.videostream.viewmodel.SharedViewModel
import com.example.videostream.viewmodel.VideoDatabaseViewModelFactory
import com.example.videostream.viewmodel.VideoViewModel


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    //recycler
    private lateinit var videoRecycler : RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private var videoList : MutableList<Video> = mutableListOf()
    private var offset: Int = 0

    //view models
    private lateinit var videoViewModel : VideoViewModel
    private lateinit var roomVideoViewModel : RoomVideoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialize
        val dataBase = DataBase.getDatabase(requireActivity())
        val videoDao = dataBase.videoDao()
        val videoRep = VideoDataBaseRepository(videoDao , VideoMapper())

        videoViewModel = ViewModelProvider(requireActivity()).get(VideoViewModel::class.java)
        roomVideoViewModel = ViewModelProvider(requireActivity() ,
            VideoDatabaseViewModelFactory(videoRep) ).get(
            RoomVideoViewModel::class.java
        )

        //////set user
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.currentUserId.observe(viewLifecycleOwner) { userId ->
            binding.mainUserText.text = if (userId == 1) {
                getString(R.string.first_user)
            } else {
                getString(R.string.second_user)
            }
        }

        //set recycler
        videoRecycler = binding.homeVideoRecycler
        videoRecycler.layoutManager = LinearLayoutManager(requireContext())

        videoAdapter = VideoAdapter(requireContext() , videoList , roomVideoViewModel , sharedViewModel , this) { _, video ->
            FragmentChanging.passVideoToFragment(
                parentFragmentManager,
                video,
            )

        }
        videoRecycler.adapter = videoAdapter

        sharedViewModel.currentUserId.observe(viewLifecycleOwner) {
            videoAdapter.setCurrentUserId(it)
        }

        roomVideoViewModel.likedIds.observe(viewLifecycleOwner) {
            videoAdapter.setLikedIds(it)
        }

        //load videos
        videoViewModel.videos.observe(viewLifecycleOwner){videos ->
            videoList.clear()
            videoList.addAll(videos)
            videoAdapter.notifyDataSetChanged()

        }

        videoViewModel.getVideos(offset)

        videoRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if(lastVisiblePosition == totalItemCount - 1){
                    offset += 5
                    videoViewModel.getVideos(offset)
                }
            }
        })

        //change user
        binding.mainUserSelectLayout.setOnClickListener{
            val users = UsersFragment()
            users.show(parentFragmentManager, "UserBottomSheet" )
        }
        binding.homeLikedLayout.setOnClickListener{
            FragmentChanging.change(parentFragmentManager , LikedFragment())
        }
    }


    private fun updateUserText() {
        val sharedPref = requireActivity().getSharedPreferences("appPref", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getInt("current_user", 1)

        binding.mainUserText.text = if (currentUserId == 1) {
            getString(R.string.first_user)
        } else {
            getString(R.string.second_user)
        }
    }

}