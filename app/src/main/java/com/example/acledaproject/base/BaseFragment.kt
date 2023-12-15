package com.example.acledaproject.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>  : Fragment() {

    protected lateinit var mBinding: T
    abstract val layoutResource: Int

    var mActivity: Activity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mActivity == null && context is BaseActivity) {
            mActivity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutResource, container, false)
        return mBinding.root
    }

}
