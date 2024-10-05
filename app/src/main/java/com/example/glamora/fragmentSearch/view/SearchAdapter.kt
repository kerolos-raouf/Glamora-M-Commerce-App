package com.example.glamora.fragmentSearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glamora.R
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.databinding.SearchItemBinding

class ProductDiffUtil : DiffUtil.ItemCallback<ProductDTO>() {
    override fun areItemsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
        return oldItem == newItem
    }
}

class SearchAdapter(private val listener: SearchClickListener) : ListAdapter<ProductDTO, SearchAdapter.ViewHolder>(ProductDiffUtil()) {

    class ViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductDTO, listener: SearchClickListener) {
            Glide.with(binding.productImg.context)
                .load(product.mainImage)
                .into(binding.productImg)

            binding.ProductName.text = product.title

            binding.root.setOnClickListener {
                listener.onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, listener)
    }
}

