package com.example.glamora.mainActivity.view

import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sharedViewModel.fetchProducts()
        sharedViewModel.fetchPriceRules()
        sharedViewModel.fetchDiscountCodes()
        sharedViewModel.fetchCurrentCustomer()
        sharedViewModel.getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP)




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
                        Snackbar.make(binding.root, "Back online", Snackbar.LENGTH_SHORT).show()
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

        //binding.bottomNavigationView.setupWithNavController(navController)
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)
    }

    override fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    override fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    override fun isInternetAvailable(): Boolean = sharedViewModel.internetState.value == ConnectivityObserver.InternetState.AVAILABLE

    override fun isLocationPermissionGranted() : Boolean
    {
        return (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    override fun requestLocationPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun isGPSEnabled() : Boolean {
        val locationManager : LocationManager = getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    ///pay pal
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("Kerolos", "onNewIntent: $intent")

        intent.data?.let { uri ->
            val opType = uri.getQueryParameter("opType")
            val token = uri.getQueryParameter("token")
            val payerId = uri.getQueryParameter("PayerID")

            Log.d("Kerolos", "Operation Type: $opType, Token: $token, PayerID: $payerId")
            sharedViewModel.operationDoneWithPayPal.value = true

            when (opType) {
                "payment" -> {
                    if (token != null && payerId != null) {
                        //captureOrder(token, payerId)
                    } else {
                        Log.e("Kerolos", "Token or PayerID is null.")
                    }
                }
                "cancel" -> Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                else -> Log.e("Kerolos", "Unknown operation type: $opType")
            }
        } ?: run {
            Log.e("Kerolos", "Intent data is null.")
        }
    }


}