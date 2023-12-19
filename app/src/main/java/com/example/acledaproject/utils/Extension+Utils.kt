package com.example.acledaproject.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.acledaproject.R

fun String.urlResource(resourceId: Int): String {
    return Uri.parse("android.resource://$this/$resourceId").toString()
}

fun ImageView.initImage(value : String?) {
    Glide.with(this).load(value ?: R.drawable.baseline_home_24)
        .placeholder(R.drawable.baseline_home_24)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun Activity.getWidth(): Int {
    val displayMetrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.initToast(msg : String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}
