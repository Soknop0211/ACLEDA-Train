package com.example.acledaproject.ui.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseActivity
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityQrcodeBinding

class QRCodeActivity : BaseBindActivity<ActivityQrcodeBinding>() {

    companion object {
        fun start(mContext : Context) {
            mContext.startActivity(Intent(mContext, QRCodeActivity::class.java))
        }
    }

    override val layoutId = R.layout.activity_qrcode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.iconBack.setOnClickListener {
            finish()
        }
    }
}