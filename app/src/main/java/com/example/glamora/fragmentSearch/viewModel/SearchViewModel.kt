package com.example.glamora.fragmentSearch.viewModel

import androidx.lifecycle.ViewModel
import com.example.glamora.data.model.ProductDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _filteredResults = MutableSharedFlow<List<ProductDTO>>()
    val filteredResults = _filteredResults.asSharedFlow()

    private val originalList = emptyList<ProductDTO>()

    fun filterList(query: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val results = if (query.isEmpty()) {
                //originalList
                emptyList()
            } else {
                originalList.filter { it.title.contains(query, ignoreCase = true) }
            }
            _filteredResults.emit(results)
        }
    }
}
