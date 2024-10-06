package com.example.glamora.fragmentSearch.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.databinding.SearchItemBinding

class ProductDiffUtil : DiffUtil.ItemCallback<ProductDTO>() {
    override fun areItemsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductDTO, newItem: ProductDTO): Boolean {
        return oldItem == newItem
    }
}

class SearchAdapter(private val listener: SearchClickListener) : ListAdapter<ProductDTO, SearchAdapter.ViewHolder>(ProductDiffUtil()) {

    class ViewHolder(private val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductDTO, listener: SearchClickListener) {
            binding.product = product
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, listener)
    }
}
