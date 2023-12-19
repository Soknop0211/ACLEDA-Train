package com.example.acledaproject.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityNotificationBinding

class NotificationActivity : BaseBindActivity<ActivityNotificationBinding>() {

    override val layoutId = R.layout.activity_notification

    companion object {
        fun start(mContext : Context) {
            mContext.startActivity(Intent(mContext, NotificationActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        val galleryViewModel =
            ViewModelProvider(this).get(NotificationViewModel::class.java)


    }

    private fun initView() {
        mBinding.toolbar.iconBack.setOnClickListener {
            finish()
        }

        mBinding.toolbar.titleToolbar.text = "Notification"
    }
}