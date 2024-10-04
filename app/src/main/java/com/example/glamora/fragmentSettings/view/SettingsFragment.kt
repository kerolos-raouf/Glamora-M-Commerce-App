package com.example.glamora.fragmentSettings.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.glamora.R
import com.example.glamora.databinding.FragmentSettingsBinding
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {


    private lateinit var binding : FragmentSettingsBinding
    private val sharedViewModel : SharedViewModel by activityViewModels()


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
    }



    private fun initViews(){
        binding.settingsBackButton.setOnClickListener {
            findNavController().popBackStack()
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
                    sharedViewModel.setSharedPrefString(Constants.CURRENCY_KEY,currencyList[position])
                    sharedViewModel.setSharedPrefString(Constants.CURRENCY_SELECTION_VALUE_KEY,position.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }



    }



}