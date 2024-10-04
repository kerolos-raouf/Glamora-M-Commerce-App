package com.example.glamora.fragmentHome.view

import com.example.glamora.data.model.DiscountCodeDTO

interface DiscountCodeListener {
    fun onDiscountCodeClicked(discountCode: DiscountCodeDTO)
}