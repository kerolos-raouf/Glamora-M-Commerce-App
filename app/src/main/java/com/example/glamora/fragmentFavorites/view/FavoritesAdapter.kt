package com.example.glamora.fragmentFavorites.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.databinding.ItemFavoritesBinding

class FavoriteDiffUtil : DiffUtil.ItemCallback<FavoriteItemDTO>() {
    override fun areItemsTheSame(oldItem: FavoriteItemDTO, newItem: FavoriteItemDTO): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: FavoriteItemDTO, newItem: FavoriteItemDTO): Boolean {
        return oldItem == newItem
    }
}

class FavoritesAdapter(private val listener: FavoritesClickListener) : ListAdapter<FavoriteItemDTO, FavoritesAdapter.ViewHolder>(FavoriteDiffUtil()) {

    class ViewHolder(private val aBinding: ItemFavoritesBinding) : RecyclerView.ViewHolder(aBinding.root) {
        fun bind(product: FavoriteItemDTO, listener: FavoritesClickListener) {

            Glide.with(aBinding.img.context)
                .load(product.image)
                .into(aBinding.img)

            aBinding.title.text = product.title
            aBinding.newPrice.text = product.price

            aBinding.cartItemDeleteButton.setOnClickListener {
                listener.onDeleteClick(product)
            }

            aBinding.root.setOnClickListener {
                listener.onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val aBinding = ItemFavoritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(aBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)

        holder.bind(product, listener)
    }
}
