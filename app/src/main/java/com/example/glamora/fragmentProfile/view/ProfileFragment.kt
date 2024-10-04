package com.example.glamora.fragmentProfile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentProfileBinding
import com.example.glamora.mainActivity.view.Communicator


class ProfileFragment : Fragment() {


    private lateinit var binding : FragmentProfileBinding

    private val communicator by lazy { requireActivity() as Communicator }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView()
    {
        binding.profileSettingsLayout.setOnClickListener {
            communicator.hideBottomNav()
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        communicator.showBottomNav()
    }

}