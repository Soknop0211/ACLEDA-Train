package com.example.acledaproject.utils

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import java.util.Locale

class MessageUtils {
    companion object {

        fun alertErrorConfirm(activity: Activity, mTitle : String, msg: String, mListener: () -> Unit) {
            if (!activity.isFinishing) {
                val builder =
                    AlertDialog.Builder(activity)
                builder.setTitle(mTitle)
                builder.setMessage(msg)
                builder.setCancelable(false)
                builder.setPositiveButton(String.format(Locale.US, "%s", "OK"))
                { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()

                    mListener.invoke()
                }
                builder.show()
            }
        }

    }
}