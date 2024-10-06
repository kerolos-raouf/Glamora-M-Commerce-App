package com.example.glamora.fragmentCart.view

import com.example.glamora.data.model.CartItemDTO

interface CartItemInterface {

    fun onItemPlusClicked(item: CartItemDTO)
    fun onItemMinusClicked(item: CartItemDTO)
    fun onItemDeleteClicked(item: CartItemDTO)
    fun onAddToFavoriteClicked(item: CartItemDTO)
    fun onItemClicked(item: CartItemDTO)

}