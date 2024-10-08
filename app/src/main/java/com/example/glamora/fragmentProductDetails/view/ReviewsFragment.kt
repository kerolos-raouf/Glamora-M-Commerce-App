package com.example.glamora.fragmentProductDetails.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.glamora.R
import com.example.glamora.data.model.Reviewer
import com.example.glamora.databinding.FragmentReviewsBinding
import com.example.glamora.fragmentProductDetails.view.adapters.ReviewsAdapter

class ReviewsFragment : Fragment() {

    private lateinit var aBinding: FragmentReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        aBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews, container, false)

        aBinding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()

        return aBinding.root
    }

    private fun setupRecyclerView() {
        val adapter = ReviewsAdapter(listOf(
            Reviewer("John Doe", R.drawable.ic_user, getString(R.string.review_one), 4.5f),
            Reviewer("Jane Smith", R.drawable.ic_user, getString(R.string.review_two), 3f),
            Reviewer("Laura Octavian", R.drawable.ic_user, getString(R.string.review_three), 4f),
            Reviewer("Jhonson Bridge", R.drawable.ic_user, getString(R.string.review_four), 3.8f),
            Reviewer("Griffin Rod", R.drawable.ic_user, getString(R.string.review_one), 5f)
        ))
        aBinding.recReviews.adapter = adapter
    }
}
