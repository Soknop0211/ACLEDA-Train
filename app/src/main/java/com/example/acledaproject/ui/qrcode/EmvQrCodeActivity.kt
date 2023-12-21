package com.example.acledaproject.ui.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityEmvQrCodeBinding
import com.example.acledaproject.ui.qrcode.adapter.CodeDecodeEmvAdapter
import com.example.acledaproject.utils.MessageUtils
import com.example.acledaproject.utils.Util
import com.example.acledaproject.viewmodel.DataList
import com.example.acledaproject.viewmodel.EmvViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.security.AccessController.getContext


class EmvQrCodeActivity : BaseBindActivity<ActivityEmvQrCodeBinding>() {

    override val layoutId = R.layout.activity_emv_qr_code

    companion object {
        fun start(mContext : Context) {
            mContext.startActivity(Intent(mContext, EmvQrCodeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.iconBack.setOnClickListener {
            finish()
        }

        // init view model
        val mEmvViewModel = ViewModelProvider(this)[EmvViewModel::class.java]

        mEmvViewModel.encodeGenerateQR()

        mEmvViewModel.qrCodeSt.observe(this) {

            // Init Generate Qr 1
            val qrCodeImageBitmap: Bitmap? = Util.getQRCodeImage512(it)
            if (qrCodeImageBitmap != null) {
                mBinding.imageQr.setImageBitmap(qrCodeImageBitmap)
            }

            val encoded = ("00020101021102160004hoge0104abcd520441115303156540523"
                    + ".7255020256035005802CN5914BEST TRANSPORT6007BEIJING6107123456762800205"
                    + "678900305098760505abcde0705klmno0805pqres0903tuv1004abcd50160004123401"
                    + "04ijkl64280002ZH0102北京0204最佳运输0304abcd65020080320016A0112233449988"
                    + "7707081234567863046325")

            /*// Init Generate Qr 2
            val barcodeEncoder = BarcodeEncoder()

            try {
                val qrCode = barcodeEncoder.encodeBitmap(it, BarcodeFormat.QR_CODE, 512, 512)
                mBinding.imageQr.setImageBitmap(qrCode)
            } catch (e: WriterException) {
                e.printStackTrace()
            }*/
        }

        // Display Tag
        // mEmvViewModel.devideTag()
        mEmvViewModel.listEncode.observe(this) {
            // MessageUtils.initAlertDialog(this@EmvQrCodeActivity, it)
        }

    }

}