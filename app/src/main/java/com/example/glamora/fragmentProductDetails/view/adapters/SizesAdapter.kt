package com.example.glamora.fragmentProductDetails.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.R
import com.example.glamora.data.model.AvailableProductsModel
import com.example.glamora.databinding.ItemProductsSizesBinding

class SizesAdapter(
    private val availableProducts: List<AvailableProductsModel>,
    private val onSizeSelected: (variant: AvailableProductsModel) -> Unit
) : RecyclerView.Adapter<SizesAdapter.SizeViewHolder>() {


    private val sizes: List<String> = availableProducts.map { it.size.toString() }.distinct()

    private var selectedPosition: Int = 0

    inner class SizeViewHolder(private val binding: ItemProductsSizesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, isSelected: Boolean) {
            binding.size = size
            binding.executePendingBindings()

            val cardView = binding.cardView

            if (isSelected) {
                cardView.setCardBackgroundColor(binding.root.context.getColor(R.color.dark_blue))
            } else {
                cardView.setCardBackgroundColor(binding.root.context.getColor(R.color.light_blue))
            }


            binding.root.setOnClickListener {
                val selectedProduct = availableProducts.find { it.size.toString() == size }
                selectedProduct?.let {
                    onSizeSelected(it)
                }

                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductsSizesBinding.inflate(inflater, parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val size = sizes[position]
        holder.bind(size, position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return sizes.size
    }
}
