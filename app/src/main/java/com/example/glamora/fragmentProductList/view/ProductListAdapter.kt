package com.example.glamora.fragmentProductList.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.R
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.databinding.ItemListBinding

class ProductListAdapterr(private var productList: List<ProductDTO>) : RecyclerView.Adapter<ProductListAdapterr.ProductListViewHolder>() {
    inner class ProductListViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding: ItemListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_list, parent, false
        )
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.product = product
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newProductList: List<ProductDTO>) {
        productList = newProductList
        notifyDataSetChanged()
    }
}