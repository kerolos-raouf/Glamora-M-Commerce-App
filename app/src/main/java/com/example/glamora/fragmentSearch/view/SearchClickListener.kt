package com.example.glamora.fragmentSearch.view

import com.example.glamora.data.model.ProductDTO

interface SearchClickListener {
    fun onItemClick(product: ProductDTO)
}