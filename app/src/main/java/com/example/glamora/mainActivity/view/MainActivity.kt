package com.example.glamora.mainActivity.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.glamora.R
import com.example.glamora.databinding.ActivityMainBinding
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var navController : NavController

    private val sharedViewModel : SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedViewModel.fetchProducts()
        sharedViewModel.fetchPriceRules()
        sharedViewModel.fetchDiscountCodes()
        sharedViewModel.fetchCurrentCustomer()
        Log.d("Kerolos", "onCreate: ${sharedViewModel.getCurrentCurrency()}")
        sharedViewModel.setCurrentCurrency(Constants.USD)
        Log.d("Kerolos", "onCreate: ${sharedViewModel.getCurrentCurrency()}")


        initView()
    }

    private fun initView()
    {
        binding.lifecycleOwner = this
        navController = findNavController(R.id.fragmentContainer)

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}