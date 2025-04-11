package com.example.videostream.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.videostream.R
import com.example.videostream.databinding.FragmentLikedBinding

class LikedFragment : Fragment() {
    private lateinit var binding : FragmentLikedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLikedBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
}