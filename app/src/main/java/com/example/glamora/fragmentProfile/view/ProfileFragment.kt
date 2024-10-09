package com.example.glamora.fragmentProfile.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentProfileBinding
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    //view model
    private val sharedViewModel : SharedViewModel by activityViewModels()
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
        binding.profileOrdersLayout.setOnClickListener {
            if (sharedViewModel.currentCustomerInfo.value.displayName == Constants.UNKNOWN) {
                showSignInDialog()
            } else {
                communicator.hideBottomNav()
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            }
        }
        if(sharedViewModel.currentCustomerInfo.value.displayName != Constants.UNKNOWN)
            binding.profileUsername.text = "Hello, ${sharedViewModel.currentCustomerInfo.value.displayName}"
    }

    override fun onStart() {
        super.onStart()
        communicator.showBottomNav()
    }

    private fun showSignInDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Please sign in to view your orders.")
            .setPositiveButton("Sign In") { dialog, _ ->
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}