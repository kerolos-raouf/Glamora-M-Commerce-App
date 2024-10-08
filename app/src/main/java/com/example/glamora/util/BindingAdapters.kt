package com.example.glamora.util

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
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

@BindingAdapter("app:setText")
fun setText(view: TextView,text : String)
{
    view.text = text
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

@BindingAdapter("cardBackgroundColor")
fun setCardBackgroundColor(cardView: CardView, colorString: String?) {
    try {
        colorString?.let {
            val color = Color.parseColor(it)
            cardView.setCardBackgroundColor(color)
        }
    } catch (e: IllegalArgumentException) {
        cardView.setCardBackgroundColor(Color.GRAY)
    }
}