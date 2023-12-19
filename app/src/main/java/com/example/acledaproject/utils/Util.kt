package com.example.acledaproject.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Hashtable

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

        fun getQRCodeImage512(width : Int, text: String): Bitmap? {
            val hints = Hashtable<EncodeHintType, String?>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
                val w = bitMatrix.width
                val h = bitMatrix.height
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
                for (x in 0 until w) {
                    for (y in 0 until h) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                return bmp
            } catch (e: WriterException) {
                e.printStackTrace()
                Log.e(e.javaClass.name, e.message + "")
            }
            return null
        }
    }
}