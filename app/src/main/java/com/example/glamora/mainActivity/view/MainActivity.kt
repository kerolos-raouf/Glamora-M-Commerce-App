package com.example.glamora.mainActivity.view

import android.os.Bundle
import android.util.Log
import android.view.View
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
class MainActivity : AppCompatActivity(), Communicator {

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


        initView()
    }

    private fun initView()
    {
        binding.lifecycleOwner = this
        navController = findNavController(R.id.fragmentContainer)

        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }
}