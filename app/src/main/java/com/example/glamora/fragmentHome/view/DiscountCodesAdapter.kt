package com.example.glamora.fragmentHome.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glamora.R
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.databinding.OffersItemBinding

class DiffUtilCallback : DiffUtil.ItemCallback<DiscountCodeDTO>() {
    override fun areItemsTheSame(oldItem: DiscountCodeDTO, newItem: DiscountCodeDTO) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: DiscountCodeDTO, newItem: DiscountCodeDTO) = oldItem == newItem
}

class DiscountCodesAdapter (
    private val discountImages: List<Int>,
    private val listener: DiscountCodeListener
) : ListAdapter<DiscountCodeDTO, ViewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.offers_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            Glide.with(offerImageCardview).load(discountImages[position%discountImages.size]).into(offerImageCardview)
            textviewSaleCardview.text = "Super Flash Sale \n ${item.percentage}% Off"
            root.setOnClickListener {
                listener.onDiscountCodeClicked(item)
            }
        }
    }
}

class ViewHolder(view : View) : RecyclerView.ViewHolder(view)
{
    val binding = OffersItemBinding.bind(view)
}