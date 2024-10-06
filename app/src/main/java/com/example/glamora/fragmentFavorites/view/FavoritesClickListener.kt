package com.example.glamora.fragmentFavorites.view
import com.example.glamora.data.model.ProductDTO

interface FavoritesClickListener {
    fun onDeleteClick(product: ProductDTO)
    fun onItemClick(product: ProductDTO)
}