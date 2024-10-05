package com.example.glamora.fragmentMap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.util.State
import com.example.glamora.data.model.citiesModel.CityForSearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _citiesForSearch = MutableStateFlow<List<CityForSearchItem>>(emptyList())
    val citiesForSearch: StateFlow<List<CityForSearchItem>> = _citiesForSearch


    fun getCitiesForSearch(name: String) {
        viewModelScope.launch {
            repository.getCitiesForSearch(name).collect {
                when(it)
                {
                    is State.Error -> {

                    }
                    State.Loading -> {

                    }
                    is State.Success -> {
                        _citiesForSearch.value = it.data
                    }
                }
            }
        }
    }

}