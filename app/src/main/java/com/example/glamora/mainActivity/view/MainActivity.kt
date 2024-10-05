package com.example.glamora.mainActivity.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.glamora.R
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.databinding.ActivityMainBinding
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        initObservers()
    }


    private fun initObservers()
    {
        lifecycleScope.launch(Dispatchers.Default) {
            repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                sharedViewModel.internetState.collect {
                    if(it == ConnectivityObserver.InternetState.AVAILABLE){
                        Snackbar.make(binding.root, "Internet Connected", Snackbar.LENGTH_SHORT).show()
                    }else {
                        Snackbar.make(binding.root, "Connection Lost", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initView()
    {
        binding.lifecycleOwner = this
        navController = findNavController(R.id.fragmentContainer)

        binding.bottomNavigationView.setupWithNavController(navController)
        hideBottomNav()
    }

    override fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    override fun isInternetAvailable(): Boolean = sharedViewModel.internetState.value == ConnectivityObserver.InternetState.AVAILABLE
}