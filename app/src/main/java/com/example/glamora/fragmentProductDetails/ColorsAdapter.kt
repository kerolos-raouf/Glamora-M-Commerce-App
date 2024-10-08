package com.example.glamora.fragmentProductDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.databinding.ItemProductsColorsBinding

class ColorsAdapter(private val colorsList: List<String>, private var selectedPosition:Int = 0) : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(val binding: ItemProductsColorsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductsColorsBinding.inflate(inflater, parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorString = colorsList[holder.adapterPosition]

        holder.binding.color = colorString

        if (holder.adapterPosition == selectedPosition) {
            holder.binding.textView.visibility = View.VISIBLE
        } else {
            holder.binding.textView.visibility = View.GONE
        }

        holder.binding.cardView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Not available", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int = colorsList.size
}
