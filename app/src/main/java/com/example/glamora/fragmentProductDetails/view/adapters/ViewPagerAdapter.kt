package com.example.glamora.fragmentProductDetails.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.databinding.ItemImageBinding

class ViewPagerAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ViewPagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.binding.imageUrl = imageUrl
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}
