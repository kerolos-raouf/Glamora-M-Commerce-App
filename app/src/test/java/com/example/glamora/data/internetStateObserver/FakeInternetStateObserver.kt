package com.example.glamora.data.internetStateObserver

import kotlinx.coroutines.flow.Flow

class FakeInternetStateObserver : ConnectivityObserver {
    override fun observer(): Flow<ConnectivityObserver.InternetState> {
        TODO("Not yet implemented")
    }
}