package com.example.glamora.fragmentFavorites.view

import android.graphics.Paint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.glamora.R
import com.example.glamora.databinding.FragmentFavoritesBinding
import com.example.glamora.databinding.ItemFavoritesBinding
import com.example.glamora.fragmentFavorites.viewModel.FavoritesViewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var favoritesBinding: FragmentFavoritesBinding
    private lateinit var favoritesItemBinding: ItemFavoritesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        favoritesItemBinding = DataBindingUtil.inflate(inflater, R.layout.item_favorites, container, false)
        return favoritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesItemBinding.oldPrice.paintFlags = favoritesItemBinding.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}