package com.example.glamora.fragmentProductDetails.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.data.model.Reviewer
import com.example.glamora.databinding.ItemReviewBinding

class ReviewsAdapter(private val reviewers: List<Reviewer>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val reviewer = reviewers[position]
        holder.binding.reviewer = reviewer
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = reviewers.size
}
