package com.example.glamora.fragmentManageLocations.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentManageLocationsBinding
import com.example.glamora.fragmentManageLocations.viewModel.ManageAddressesViewModel
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageLocationsFragment : Fragment() {

    //view models
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val manageAddressesViewModel : ManageAddressesViewModel by viewModels()

    private lateinit var binding : FragmentManageLocationsBinding

    //adapter
    private lateinit var adapter : ManageLocationsRecyclerAdapter

    //custom dialog
    private lateinit var customAlertDialog : CustomAlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_locations, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = manageAddressesViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }


    private fun initViews(){
        customAlertDialog = CustomAlertDialog(requireActivity())
        adapter = ManageLocationsRecyclerAdapter{address->
            customAlertDialog.showAlertDialog(
                "Are you sure you want to delete this address?",
                "Delete"
            ){
                val newAddresses = sharedViewModel.currentCustomerInfo.value.addresses.filter { it != address}
                manageAddressesViewModel.deleteCustomerAddresses(sharedViewModel.currentCustomerInfo.value.userId,newAddresses)
            }
        }
        binding.addressesRecyclerView.adapter = adapter

        if(sharedViewModel.currentCustomerInfo.value.addresses.isNotEmpty())
        {
            adapter.submitList(sharedViewModel.currentCustomerInfo.value.addresses)
        }


        binding.manageLocationsAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_manageLocationsFragment_to_mapFragment)
        }

    }


    private fun initObservers(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                manageAddressesViewModel.customerAddresses.collect{addresses->
                    addresses?.let {
                        adapter.submitList(it)
                        sharedViewModel.currentCustomerInfo.value.addresses = it
                    }
                }
            }
        }
    }


}