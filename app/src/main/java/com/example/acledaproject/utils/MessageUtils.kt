package com.example.acledaproject.utils

import android.app.Activity
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acledaproject.R
import com.example.acledaproject.ui.qrcode.adapter.CodeDecodeEmvAdapter
import com.example.acledaproject.viewmodel.DataList
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

        fun initAlertDialog(mActivity: Activity, mList : ArrayList<DataList>, mListener : () -> Unit) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(mActivity)

            val inflater = mActivity.layoutInflater
            val dialogView = inflater.inflate(R.layout.custom_alert_dialog_list, null) as View

            builder.setView(dialogView)
            builder.setCancelable(false)

            val recyclerView = dialogView.findViewById<View>(R.id.recyclerView) as RecyclerView

            val close = dialogView.findViewById<View>(R.id.close) as ImageView

            recyclerView.apply {
                layoutManager = LinearLayoutManager(mActivity)
                adapter = CodeDecodeEmvAdapter(mActivity, mList)
            }

            val dialog: AlertDialog = builder.create()

            close.setOnClickListener {
                dialog.dismiss()
                mListener.invoke()
            }

            dialog.show()
        }

    }
}