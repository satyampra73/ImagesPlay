package com.satyam.imageplay.viewmodel

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.satyam.imageplay.R

class CustomProgressBar {
    private var dialog: Dialog? = null
    fun showProgress(context: Context?) {
        if (context != null) {
            dialog = Dialog(context)
            dialog!!.setContentView(R.layout.custom_dialog_progress)
            if (dialog!!.window != null) {
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.show()
        } else {
            // Handle the case where the context is null.
        }
    }

    fun hideProgress() {
        if (dialog != null) {
            if (dialog!!.isShowing) dialog!!.dismiss()
        }
    }
}