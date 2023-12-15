package com.example.acledaproject.utils

import android.view.View
import android.view.ViewTreeObserver

class Util {
    companion object {
        fun widthHeightLayout(layout: View, onCallBackListener: (width: Int, height: Int) -> Unit) {
            val vto = layout.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    onCallBackListener.invoke(layout.measuredWidth, layout.measuredHeight)
                }
            })
        }
    }
}