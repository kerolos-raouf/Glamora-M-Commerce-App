package com.example.glamora.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.example.glamora.R
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence

fun showGuestDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setMessage("Your in guest mode. Please login.")
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
    builder.create().show()
}


fun startTapTargetSequence(activity: Activity,targets: List<TapTarget>) {
    TapTargetSequence(activity).targets(targets).start()
}

fun getTapTargetView(view: View,title:String,description:String) : TapTarget {
    return TapTarget.forView(view, title, description)
        .outerCircleColor(R.color.light_blue)
        .outerCircleAlpha(0.8f)
        .targetCircleColor(R.color.white)
        .titleTextSize(24)
        .titleTextColor(R.color.white)
        .descriptionTextSize(20)
        .descriptionTextColor(R.color.white)
        .cancelable(false)
        .tintTarget(true)
        .transparentTarget(true)
        .targetRadius(60)
        .drawShadow(true)
        .dimColor(R.color.dark_blue)
        .transparentTarget(true)
        .targetRadius(60)
}