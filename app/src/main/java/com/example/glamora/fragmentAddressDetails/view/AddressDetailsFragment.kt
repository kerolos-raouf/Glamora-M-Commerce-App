package com.example.glamora.fragmentAddressDetails.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddressDetailsFragment : Fragment() {


    private val addressViewModel: AddressViewModel by viewModels()

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

        Log.d("Kerolos", "initView: $currentAddress")
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                addressViewModel.message.collect {
                    if(it.isNotEmpty()){
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
        addressViewModel.updateCustomerAddress(address = currentAddress)
    }

}