package com.example.glamora.fragmentManageLocations.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.glamora.R
import com.example.glamora.databinding.FragmentManageLocationsBinding
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.customAlertDialog.CustomAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageLocationsFragment : Fragment() {

    //view models
    private val sharedViewModel : SharedViewModel by activityViewModels()

    private lateinit var binding : FragmentManageLocationsBinding

    //adapter
    private lateinit var adapter : ManageLocationsRecyclerAdapter

    //custom dialog
    private lateinit var customAlertDialog : CustomAlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_locations, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }


    private fun initViews(){
        customAlertDialog = CustomAlertDialog(requireActivity())
        adapter = ManageLocationsRecyclerAdapter{
            customAlertDialog.showAlertDialog(
                "Are you sure you want to delete this address?",
                "Delete"
            ){

            }
        }
        binding.addressesRecyclerView.adapter = adapter

        if(sharedViewModel.currentCustomerInfo.value.addresses.isNotEmpty())
        {
            adapter.submitList(sharedViewModel.currentCustomerInfo.value.addresses)
        }

    }


}