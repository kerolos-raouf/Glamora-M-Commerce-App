package com.example.glamora.fragmentFavorites.view

import com.example.glamora.data.model.FavoriteItemDTO


interface FavoritesClickListener {
    fun onDeleteClick(product: FavoriteItemDTO)
    fun onItemClick(product: FavoriteItemDTO)
}