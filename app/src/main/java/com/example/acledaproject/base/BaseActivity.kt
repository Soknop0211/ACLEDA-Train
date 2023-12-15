package com.example.acledaproject.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        /*val mActionBar = supportActionBar
        if (mActionBar != null) {
            supportActionBar?.hide()
        }*/
    }

    open fun getStringExtra(key: String, context: Activity): String {
        if (context.intent != null && context.intent.hasExtra(key)) {
            return context.intent.getStringExtra(key).toString()
        }
        return ""
    }

}