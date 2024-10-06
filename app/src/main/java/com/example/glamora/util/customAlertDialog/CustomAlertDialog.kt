package com.example.glamora.util.customAlertDialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.example.glamora.R
import com.example.glamora.databinding.CustomAlertDialogBinding

class CustomAlertDialog(
    private val activity : Activity,

)
{

    private lateinit var alertDialog : AlertDialog

    fun showAlertDialog(message : String, actionText : String, action : () -> Unit)
    {
        val builder = AlertDialog.Builder(activity)
        val binding = CustomAlertDialogBinding.bind(activity.layoutInflater.inflate(R.layout.custom_alert_dialog, null))
        builder.setView(binding.root)

        binding.alertMessage.text = message
        binding.alertActionButton.text = actionText

        binding.alertCancelButton.setOnClickListener{
            alertDialog.dismiss()
        }

        binding.alertActionButton.setOnClickListener {
            action()
            alertDialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }


}