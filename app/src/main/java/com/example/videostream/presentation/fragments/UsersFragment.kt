package com.example.videostream.presentation.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.videostream.R
import com.example.videostream.databinding.FragmentUsersBinding
import com.example.videostream.viewmodel.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class UsersFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userFirstUser.text = getString(R.string.first_user)
        binding.userSecondUser.text = getString(R.string.second_user)
        //val sharedPref = requireActivity().getSharedPreferences("appPref",Context.MODE_PRIVATE)
        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.userFirstUserLayout.setOnClickListener{
            //sharedPref.edit().putInt("current_user" , 1).apply()
            sharedViewModel.setCurrentUser(1)
            Toast.makeText(context , "کاربر اول انتخاب شد" , Toast.LENGTH_SHORT).show()
            dismiss()
        }
        binding.userSecondUserLayout.setOnClickListener{
            //sharedPref.edit().putInt("current_user" , 2).apply()
            sharedViewModel.setCurrentUser(2)
            Toast.makeText(context , "کاربر دوم انتخاب شد" , Toast.LENGTH_SHORT).show()
            dismiss()
        }
        binding.userCloseIcon.setOnClickListener{
            dismiss()
        }
    }


}