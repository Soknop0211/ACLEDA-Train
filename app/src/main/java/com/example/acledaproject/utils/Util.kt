package com.example.acledaproject.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import br.com.fluentvalidator.context.ValidationResult
import com.emv.qrcode.validators.Crc16Validate
import com.example.acledaproject.viewmodel.DataList
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

        fun getQRCodeImage512(text: String): Bitmap? {
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

        fun getEncodeTag(encodeSt : String) :  ArrayList<DataList> {

            val mList = ArrayList<DataList>()
            var mData = DataList()

            try {
                var i = 0
                while (i < encodeSt.length) {
                    if (mData.num.isEmpty()){
                        mData.num = encodeSt.substring(i, i + 2)
                    } else if(mData.length.isEmpty()) {
                        mData.length = encodeSt.substring(i, i + 2)
                    } else {
                        if (mData.num == "29" || mData.num == "39" || mData.num == "40" || mData.num == "99") {
                            val number = mData.length.trimStart('0').toInt()

                            val mSubItemList = subTag(mData, encodeSt, i)
                            i += number
                            mData.mList = mSubItemList
                            mList.add(mData)
                            mData = DataList()
                            continue
                        } else {
                            try {
                                val number = mData.length.trimStart('0').toInt()
                                mData.valueItem = encodeSt.substring(i, i + number)
                                mList.add(mData)
                                mData = DataList()
                                i += number
                                continue
                            } catch (e: NumberFormatException) {
                                println("Invalid number format") // Output: Invalid number format
                            }
                        }
                    }

                    i += 2

                }
            } catch (ex : StringIndexOutOfBoundsException) {
                ex.printStackTrace()
            }

            mList.forEach {
                Log.d("logdebugdecodemodel", it.num + " : " + it.length + "=>" + it.valueItem)
            }

            return mList
        }

        private fun subTag(mData : DataList, encodeSt : String, i : Int) : ArrayList<DataList>{
            val mSubItemList = ArrayList<DataList>()
            var mSubData = DataList()
            val number = mData.length.trimStart('0').toInt()

            val mItem = encodeSt.substring(i, i + number)
            var j = 0
            while (j < mItem.length) {
                if (mSubData.num.isEmpty()){
                    mSubData.num = mItem.substring(j, j + 2)
                } else if(mSubData.length.isEmpty()) {
                    mSubData.length = mItem.substring(j, j + 2)
                } else {
                    try {
                        val subNumber = mSubData.length.trimStart('0').toInt()
                        mSubData.valueItem = mItem.substring(j, j + subNumber)
                        mSubItemList.add(mSubData)
                        mSubData = DataList()
                        j += subNumber
                        continue
                    } catch (e: NumberFormatException) {
                        println("Invalid number format") // Output: Invalid number format
                    }
                }
                j += 2
            }

            return mSubItemList
        }

        fun checkIsKHQR(resultEnCode : String?) : Boolean {
            if (resultEnCode == null) return false

            // Check encode invalid
            val validationResult : ValidationResult = Crc16Validate.validate(resultEnCode)
            if (!validationResult.isValid) {
                return false
            }

            return true
        }


    }
}