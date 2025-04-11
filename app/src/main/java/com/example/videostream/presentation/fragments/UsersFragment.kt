package com.example.videostream.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.videostream.R
import com.example.videostream.databinding.FragmentUsersBinding
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

        binding.userFirstUserLayout.setOnClickListener{
            Toast.makeText(context , "کاربر اول انتخاب شد" , Toast.LENGTH_SHORT).show()
            dismiss()
        }
        binding.userSecondUserLayout.setOnClickListener{
            Toast.makeText(context , "کاربر دوم انتخاب شد" , Toast.LENGTH_SHORT).show()
            dismiss()
        }
        binding.userCloseIcon.setOnClickListener{
            dismiss()
        }
    }


}