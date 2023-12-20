package com.example.acledaproject.ui.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityEmvQrCodeBinding
import com.example.acledaproject.utils.Util
import com.example.acledaproject.viewmodel.EmvViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

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

            // Init Generate Qr 2
            val barcodeEncoder = BarcodeEncoder()

            try {
                val qrCode = barcodeEncoder.encodeBitmap(it, BarcodeFormat.QR_CODE, 512, 512)
                mBinding.imageQr.setImageBitmap(qrCode)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }
    }

}