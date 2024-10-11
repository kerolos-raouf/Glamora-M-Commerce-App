package com.example.glamora.mainActivity.view

import com.google.android.material.bottomnavigation.BottomNavigationView

interface Communicator {

    fun hideBottomNav()

    fun showBottomNav()

    fun isInternetAvailable() : Boolean

    fun isLocationPermissionGranted() : Boolean

    fun requestLocationPermission()

    fun isGPSEnabled() : Boolean

    fun getBottomNavView() : BottomNavigationView

}