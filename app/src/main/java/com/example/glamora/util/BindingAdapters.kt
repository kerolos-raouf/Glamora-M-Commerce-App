package com.example.glamora.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.glamora.R

@BindingAdapter("app:srcCompat")
fun setImageFromUrl(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    } else {
        imageView.setImageResource(R.drawable.product)
    }
}

@BindingAdapter("app:setPrice")
fun setPrice(view: TextView,price : String)
{
    val code = view.context.getSharedPreferences(Constants.SHARED_PREF_NAME,Context.MODE_PRIVATE).getString(Constants.CURRENCY_KEY,Constants.EGP)
    val valueToMultiply = view.context.getSharedPreferences(Constants.SHARED_PREF_NAME,Context.MODE_PRIVATE).getString(Constants.CURRENCY_MULTIPLIER_KEY,1.toString()) ?: "1"
    val priceValue = String.format("%.2f",price.toDouble() * valueToMultiply.toDouble())
    view.text = "$priceValue $code"
}

@BindingAdapter("app:showProgressBar")
fun showProgressBar(view: View, show : Boolean)
{
    if(show)
    {
        view.visibility = View.VISIBLE
    }else
    {
        view.visibility = View.GONE

    }
}