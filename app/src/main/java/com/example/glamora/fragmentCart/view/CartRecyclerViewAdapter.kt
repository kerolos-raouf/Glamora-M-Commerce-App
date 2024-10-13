package com.example.glamora.fragmentCart.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glamora.R
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.databinding.ItemCartBinding

class DiffUtilCallback : DiffUtil.ItemCallback<CartItemDTO>() {
    override fun areItemsTheSame(oldItem: CartItemDTO, newItem: CartItemDTO) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: CartItemDTO, newItem: CartItemDTO) = oldItem == newItem
}

class CartRecyclerViewAdapter (
    private val listener: CartItemInterface,
) : ListAdapter<CartItemDTO, ViewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {


            Glide.with(cartItemImage).load(item.image).into(cartItemImage)
            cartItem = item
            cartItemPlusButton.setOnClickListener {
                if(item.quantity < item.inventoryQuantity)
                {
                    item.quantity++
                    cartItem = item
                    listener.onItemPlusClicked(item)
                }else{
                    listener.onReachedMaxQuantity(item)
                }
            }
            cartItemMinusButton.setOnClickListener {
                if(item.quantity > 1)
                {
                    item.quantity--
                    cartItem = item
                    listener.onItemMinusClicked(item)
                }else
                {
                    listener.onItemDeleteClicked(item)
                }
            }
            cartItemDeleteButton.setOnClickListener {
                listener.onItemDeleteClicked(item)
            }
            cartItemFavoriteButton.setOnClickListener {
                listener.onAddToFavoriteClicked(item)
            }
            root.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }
}

class ViewHolder(view : View) : RecyclerView.ViewHolder(view)
{
    val binding = ItemCartBinding.bind(view)
}