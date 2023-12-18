package com.example.acledaproject.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseBindActivity <T : ViewDataBinding> : BaseActivity() {

    protected lateinit var mBinding: T
    abstract val layoutId: Int

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, layoutId)
    }

}