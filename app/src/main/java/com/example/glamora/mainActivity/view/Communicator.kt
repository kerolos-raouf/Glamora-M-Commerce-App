package com.example.glamora.mainActivity.view

interface Communicator {

    fun hideBottomNav()

    fun showBottomNav()

    fun isInternetAvailable() : Boolean

    fun isLocationPermissionGranted() : Boolean

    fun requestLocationPermission()

    fun isGPSEnabled() : Boolean

}