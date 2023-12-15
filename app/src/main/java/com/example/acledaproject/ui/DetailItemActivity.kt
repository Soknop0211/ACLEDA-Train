package com.example.acledaproject.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.acledaproject.databinding.ActivityDetailItemBinding

class DetailItemActivity : AppCompatActivity() {

    companion object {
        fun gotoDetailItemActivity(mContext : Context, mTitle : String) {
            mContext.startActivity(
                Intent(mContext, DetailItemActivity::class.java).apply {
                    putExtra(TITLE, mTitle)
                }
            )
        }

        private const val TITLE = "TITLE"
    }

    private lateinit var binding: ActivityDetailItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        binding.toolbar.iconBack.setOnClickListener {
            finish()
        }

        binding.toolbar.titleToolbar.text = intent.getStringExtra(TITLE)
    }
}