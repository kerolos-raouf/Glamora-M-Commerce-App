package com.example.glamora.fragmentProfile.viewModel

import androidx.lifecycle.ViewModel
import com.example.glamora.data.contracts.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    fun signOut() {
        repository.signOut()
    }

}