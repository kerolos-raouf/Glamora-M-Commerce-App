package com.example.glamora.fragmentAddressDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.glamora.R
import com.example.glamora.data.model.AddressModel
import com.example.glamora.databinding.FragmentAddressDetailsBinding

class AddressDetailsFragment : Fragment() {


    private lateinit var binding : FragmentAddressDetailsBinding

    //current address
    private var currentAddress = AddressModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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
                    updateCustomerAddress()
                }else
                {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCustomerAddress() {

    }

}