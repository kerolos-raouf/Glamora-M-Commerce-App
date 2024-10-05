package com.example.glamora.fragmentHome.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.R
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.databinding.BrandsItemBinding

class BrandsAdapter (
    private var brandsList: List<Brands>,
    private val onItemClick: (Brands) -> Unit
) : RecyclerView.Adapter<BrandsAdapter.BrandsViewHolder>() {
    inner class BrandsViewHolder(val binding: BrandsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandsViewHolder {
        val binding: BrandsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.brands_item, parent, false
        )
        return BrandsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  brandsList.size
    }

    override fun onBindViewHolder(holder: BrandsViewHolder, position: Int) {
        val brands = brandsList[position]
        holder.binding.brands = brands
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            onItemClick(brands)
        }
    }

        fun updateData(newBrands: List<Brands>) {
            this.brandsList = newBrands
            notifyDataSetChanged()
        }
    }


