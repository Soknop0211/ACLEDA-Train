package com.example.acledaproject.ui.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityQrcodeBinding
import com.example.acledaproject.utils.Util

class QRCodeActivity : BaseBindActivity<ActivityQrcodeBinding>() {

    companion object {
        fun start(mContext : Context) {
            mContext.startActivity(Intent(mContext, QRCodeActivity::class.java))
        }
    }

    override val layoutId = R.layout.activity_qrcode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Back Button
        mBinding.iconBack.setOnClickListener {
            finish()
        }

        // Init Generate Qr
        Util.widthHeightLayout(mBinding.imageQr) { width, _ ->
            val qrCodeImageBitmap: Bitmap? = Util.getQRCodeImage512("SokNop@@123456789$$")
            if (qrCodeImageBitmap != null) {
                // mBinding.imageQr.setImageBitmap(qrCodeImageBitmap)
                Glide.with(this).load(qrCodeImageBitmap).into(mBinding.imageQr)
            }
        }


    }
}