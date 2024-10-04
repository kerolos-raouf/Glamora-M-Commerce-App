package com.example.glamora.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.glamora.R

object BindingAdapters {

    @BindingAdapter("app:srcCompat")
    @JvmStatic
    fun setImageFromUrl(imageView: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.product)
        }
    }
}