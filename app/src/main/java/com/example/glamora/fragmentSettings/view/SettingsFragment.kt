package com.example.glamora.fragmentSettings.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentSettingsBinding
import com.example.glamora.mainActivity.view.Communicator
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {


    private lateinit var binding : FragmentSettingsBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()

    private val communicator by lazy { requireActivity() as Communicator }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        sharedViewModel.getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP)

    }



    private fun initViews(){

        hideViewsInGuestMode()

        binding.settingsBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.settingsDeliveryLocationLayout.setOnClickListener {
            if(!communicator.isInternetAvailable()){
                Toast.makeText(requireContext(),"No Internet Connection", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    findNavController().navigate(R.id.action_settingsFragment_to_mapFragment)
                }catch (e : Exception) {
                    Log.d("Kerolos", "setupCardViews: $e")
                }
            }
        }
        binding.settingsManageLocationLayout.setOnClickListener {
            if(!communicator.isInternetAvailable()){
                Toast.makeText(requireContext(),"No Internet Connection", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    findNavController().navigate(R.id.action_settingsFragment_to_manageLocationsFragment)
                }catch (e : Exception) {
                    Log.d("Kerolos", "setupCardViews: $e")
                }
            }
        }


        //currency spinner
        val currencyList = listOf(Constants.EGP, Constants.USD, Constants.EUR, Constants.AED, Constants.SAR)
        binding.settingsCurrencySpinner.apply {
            adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, currencyList)
            setSelection(
                sharedViewModel.getSharedPrefString(
                    Constants.CURRENCY_SELECTION_VALUE_KEY,
                    Constants.EGP_SELECTION_VALUE.toString()
                ).toInt()
            )
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(!communicator.isInternetAvailable()){
                        Toast.makeText(requireContext(),"No Internet Connection, Currency not changed.",Toast.LENGTH_SHORT).show()
                    }else{
                        sharedViewModel.setSharedPrefString(Constants.CURRENCY_KEY,currencyList[position])
                        sharedViewModel.setSharedPrefString(Constants.CURRENCY_SELECTION_VALUE_KEY,position.toString())
                        sharedViewModel.convertCurrency()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }

    }

    private fun hideViewsInGuestMode(){
        if(sharedViewModel.getSharedPrefString(Constants.CUSTOMER_EMAIL,Constants.UNKNOWN) == Constants.UNKNOWN){
            binding.settingsManageLocationLayout.visibility = View.GONE
            binding.settingsDeliveryLocationLayout.visibility = View.GONE
        }
    }



}