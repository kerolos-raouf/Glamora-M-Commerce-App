package com.example.glamora.fragmentProductList.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.R
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.databinding.ItemListBinding

class ProductListAdapter(private var productList: List<ProductDTO>,
                         private val clickListener: (ProductDTO) -> Unit
    ) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {
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
        holder.itemView.setOnClickListener {
            clickListener(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newProductList: List<ProductDTO>) {
        productList = newProductList
        notifyDataSetChanged()
    }
}