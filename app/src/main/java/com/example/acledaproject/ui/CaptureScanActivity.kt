package com.example.acledaproject.ui

import android.app.Activity
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import com.example.acledaproject.R
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class CaptureScanActivity : Activity (){

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeScannerView = initializeContent()
        capture = CaptureManager(this, barcodeScannerView)
        capture?.initializeFromIntent(intent, savedInstanceState)
        capture?.decode()

        findViewById<ImageView>(R.id.iconBack).setOnClickListener {
            finish()
        }

        val iconFlash = findViewById<ImageView>(R.id.iconFlash)
        var mIsOpenFlash = false
        val cameraManager: CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

        iconFlash.setOnClickListener {
            if (mIsOpenFlash) {
                mIsOpenFlash = false
            } else {
                    mIsOpenFlash = true
            }
        }
    }

    private fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_capture_scan)
        return findViewById(R.id.zxing_barcode_scanner)
    }

    override fun onResume() {
        super.onResume()
        capture?.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}