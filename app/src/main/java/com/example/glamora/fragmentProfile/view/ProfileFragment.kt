package com.example.glamora.fragmentProfile.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.databinding.FragmentProfileBinding
import com.example.glamora.fragmentProfile.viewModel.ProfileViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import com.example.glamora.util.showGuestDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    //view model
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private lateinit var binding : FragmentProfileBinding

    private val communicator by lazy { requireActivity() as Communicator }

    private lateinit var customAlertDialog: CustomAlertDialog

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
        customAlertDialog = CustomAlertDialog(requireActivity())
        binding.profileSettingsLayout.setOnClickListener {
            communicator.hideBottomNav()
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        binding.profileOrdersLayout.setOnClickListener {
            Log.d("Kerolos", "initView: ${sharedViewModel.currentCustomerInfo.value.email}")
            if (sharedViewModel.currentCustomerInfo.value.email == Constants.UNKNOWN) {
                showGuestDialog(requireContext())
            } else {
                communicator.hideBottomNav()
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            }
        }
        if(sharedViewModel.currentCustomerInfo.value.displayName != Constants.UNKNOWN)
            binding.profileUsername.text = "Hello, ${sharedViewModel.currentCustomerInfo.value.displayName}"

        binding.profileLogOutLayout.setOnClickListener {
            customAlertDialog.showAlertDialog("Are you sure you want to log out?", "Log Out"){
                logOutActions()
            }
        }
    }


    private fun logOutActions()
    {
        sharedViewModel.setSharedPrefString(Constants.CUSTOMER_EMAIL,Constants.UNKNOWN)
        sharedViewModel.setCustomerInfo(CustomerInfo())
        sharedViewModel.setFavoriteWithEmptyList()
        profileViewModel.signOut()
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }

    override fun onStart() {
        super.onStart()
        communicator.showBottomNav()
    }



}