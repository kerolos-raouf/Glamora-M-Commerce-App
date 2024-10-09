package com.example.glamora.fragmentAddressDetails.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.data.model.AddressModel
import com.example.glamora.databinding.FragmentAddressDetailsBinding
import com.example.glamora.fragmentAddressDetails.viewModel.AddressViewModel
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddressDetailsFragment : Fragment() {


    private val addressViewModel: AddressViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var binding : FragmentAddressDetailsBinding

    //current address
    private var currentAddress = AddressModel()


    private val communicator : Communicator by lazy {
        (requireActivity() as Communicator)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = addressViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initView() {
        currentAddress = AddressDetailsFragmentArgs.fromBundle(requireArguments()).addressModel

        if(sharedViewModel.currentCustomerInfo.value.email != Constants.UNKNOWN)
        {
            addressViewModel.getCustomerAddressesByEmail(sharedViewModel.currentCustomerInfo.value.email)
        }


        binding.apply {
            addressAddButton.setOnClickListener {
                if(addressFirstName.text.isNotEmpty()
                    && addressLastName.text.isNotEmpty()
                    && addressPhone.text.isNotEmpty()
                    && addressStreet.text.isNotEmpty())
                {
                    if(communicator.isInternetAvailable())
                    {
                        fillCurrentAddress()
                        updateCustomerAddress()
                    }else
                    {
                        Toast.makeText(requireContext(),"No Internet Connection",Toast.LENGTH_SHORT).show()
                    }

                }else
                {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addressBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initObservers() {
        addressViewModel.message.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addressViewModel.address.collect {
                    if(sharedViewModel.currentCustomerInfo.value.email != Constants.UNKNOWN)
                    {
                        sharedViewModel.currentCustomerInfo.value.addresses = it
                    }
                }
            }
        }
    }

    private fun fillCurrentAddress() {
        binding.apply {
            currentAddress.firstName = addressFirstName.text.toString()
            currentAddress.lastName = addressLastName.text.toString()
            currentAddress.phone = addressPhone.text.toString()
            currentAddress.street = addressStreet.text.toString()
        }
    }

    private fun updateCustomerAddress() {
        if (sharedViewModel.currentCustomerInfo.value.userId != Constants.UNKNOWN)
        {
            addressViewModel.updateCustomerAddress(
                sharedViewModel.currentCustomerInfo.value.userId,
                sharedViewModel.currentCustomerInfo.value.email,
                currentAddress)
            clearFields()
        }
    }

    private fun clearFields() {
        binding.apply {
            addressFirstName.text.clear()
            addressLastName.text.clear()
            addressPhone.text.clear()
            addressStreet.text.clear()
        }
    }

}