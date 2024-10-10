package com.example.glamora.util

import android.app.AlertDialog
import android.content.Context

fun showGurstDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setMessage("Your in guest mode. Please login.")
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
    builder.create().show()
}