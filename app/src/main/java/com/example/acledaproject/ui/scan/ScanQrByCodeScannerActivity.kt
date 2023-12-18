package com.example.acledaproject.ui.scan

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityScanQrByCodeScannerBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ScanQrByCodeScannerActivity : BaseBindActivity<ActivityScanQrByCodeScannerBinding>() {

    private lateinit var codeScanner: CodeScanner

    override val layoutId = R.layout.activity_scan_qr_by_code_scanner

    private var mDisposable: Disposable? = null
    private var isCodeScanner = false
    companion object {
        fun start(mContext : Context, mAction : String) {
            mContext.startActivity(Intent(mContext, ScanQrByCodeScannerActivity::class.java).apply {
                putExtra(ACTION, mAction)
            })
        }

        private const val ACTION = "ACTION"
        const val CodeScannerSt = "CodeScannerSt"
        const val SimpleBarcodeScanner = "SimpleBarcodeScanner"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.getStringExtra(ACTION).toString() == CodeScannerSt) {
            isCodeScanner = true
            initCodeScanner()
        } else {
            mBinding.barcodeView.visibility = View.VISIBLE
            isCodeScanner = false
        }
    }

    private fun initCodeScanner() {
        val scannerView = mBinding.scannerView
        scannerView.visibility = View.VISIBLE

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "${it.text}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isCodeScanner) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (isCodeScanner) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    override fun onStart() {
        super.onStart()

        if (!isCodeScanner) {
            mDisposable = mBinding.barcodeView
                .getObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { barcode ->
                        Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    { throwable ->
                        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
                    })
        }

    }

    override fun onStop() {
        super.onStop()
        if (!isCodeScanner) {
            mDisposable?.dispose()
        }
    }

}