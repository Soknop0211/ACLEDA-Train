package com.example.acledaproject.ui.scan

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.fluentvalidator.context.ValidationResult
import com.emv.qrcode.decoder.mpm.DecoderMpm
import com.emv.qrcode.model.mpm.MerchantPresentedMode
import com.emv.qrcode.validators.Crc16Validate
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityScanQrByCamViewBinding
import com.example.acledaproject.utils.MessageUtils
import com.example.acledaproject.utils.Util
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class ScanQrByServiceGoogleActivity : BaseBindActivity<ActivityScanQrByCamViewBinding>() {

    companion object {
        fun start(mContext : Context) {
            mContext.startActivity(Intent(mContext, ScanQrByServiceGoogleActivity::class.java))
        }

    }

    private lateinit var cameraSource: CameraSource

    override val layoutId = R.layout.activity_scan_qr_by_cam_view

    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private var valueType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aniSlide: Animation = AnimationUtils.loadAnimation(
            this, R.anim.scanner_animation)
        mBinding.barcodeLine.startAnimation(aniSlide)

        setupControls()

        mBinding.rescan.setOnClickListener {
            mBinding.cameraSurfaceView.holder?.let { it1 -> if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@let
            }
                cameraSource.start(it1) }
            mBinding.barcodeLine.startAnimation(aniSlide)
            mBinding.rescan.visibility = View.GONE
        }

        mBinding.scannedText.setOnLongClickListener(View.OnLongClickListener {
            copyToClipboard(
                mBinding.scannedText.text.toString(),
                mBinding.root
            )
            Toast.makeText(
                this,
                "copy_to_clipboard",
                Toast.LENGTH_SHORT
            ).show()
            true
        })

        mBinding.overlay.post {
            mBinding.overlay.setViewFinder()
        }

        // Back Button
        mBinding.toolbar.iconBack.setOnClickListener {
            finish()
        }

    }

    private fun copyToClipboard(text: CharSequence, view:View){
        val clipboard = ContextCompat.getSystemService(view.context, ClipboardManager::class.java)
        clipboard?.setPrimaryClip(ClipData.newPlainText("",text))
    }
    private fun setupControls(){
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this,barcodeDetector)
            .setRequestedPreviewSize(1920,1080)
            .setAutoFocusEnabled(true)
            .build()

        mBinding.cameraSurfaceView.holder?.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(p0: SurfaceHolder) {
                try {
                    //start preview after 1s delay
                    if (ActivityCompat.checkSelfPermission(
                            this@ScanQrByServiceGoogleActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    cameraSource.start(p0)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScanQrByServiceGoogleActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    cameraSource.start(p0)
                }catch (e: Exception){
                    e.printStackTrace()
                }

            }
            override fun surfaceDestroyed(p0: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object: Detector.Processor<Barcode>{
            override fun release() {
                Toast.makeText(this@ScanQrByServiceGoogleActivity,"Scanner has been closed!", Toast.LENGTH_LONG).show()
            }
            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val barcodes = p0.detectedItems
                if(barcodes.size()>0){
                    val scannedBarcode: Barcode = barcodes.valueAt(0)
                    if(barcodes.size() ==1){
                        Log.d("Scanner","${scannedBarcode.format}, ${scannedBarcode.valueFormat}")
                        scannedValue = scannedBarcode.rawValue
                        valueType = scannedBarcode.valueFormat
                        runOnUiThread {
                            cameraSource.stop()
                            mBinding.barcodeLine.clearAnimation()
                            mBinding.barcodeLine.visibility = View.GONE
                            // mBinding.rescan.visibility = View.VISIBLE
                            checkValueType(scannedBarcode,valueType)
                        }
                    }else{
                        Toast.makeText(this@ScanQrByServiceGoogleActivity,"value -else", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun checkValueType(scannedBarcode: Barcode, valueType:Int){
        when(valueType){
            Barcode.WIFI ->{
                mBinding.scannedText.text = "SSID - ${scannedBarcode.wifi.ssid} \n" +
                        "Password - ${scannedBarcode.wifi.password} \n" +
                        "Encryption type - ${scannedBarcode.wifi.encryptionType}"
            }
            Barcode.URL ->{
                Toast.makeText(this, scannedBarcode.url.title, Toast.LENGTH_SHORT).show()
                mBinding.scannedText.text = "URL -${scannedBarcode.url.title}, ${scannedBarcode.url.url}"
            }
            Barcode.PRODUCT ->{
                Toast.makeText(this, scannedBarcode.displayValue, Toast.LENGTH_SHORT).show()
                mBinding.scannedText.text = "Product - ${scannedBarcode.displayValue}"
            }
            Barcode.EMAIL ->{
                Toast.makeText(this, scannedBarcode.email.address, Toast.LENGTH_SHORT).show()
                mBinding.scannedText.text = "Email address- ${scannedBarcode.email.address}\n, " +
                        "${scannedBarcode.email.subject}\n,${scannedBarcode.email.body}\n," +
                        "${scannedBarcode.email.type}"
            }
            Barcode.PHONE -> {
                Toast.makeText(this, scannedBarcode.phone.number, Toast.LENGTH_SHORT).show()
                mBinding.scannedText.text = "Phone number- ${scannedBarcode.phone.number}"
            }
            else ->{
                mBinding.scannedText.text = scannedValue
                if (Util.checkIsKHQR(scannedValue)) {    // KHQR
                    /*val mDataDecode : MerchantPresentedMode = DecoderMpm.decode(
                        scannedValue,
                        MerchantPresentedMode::class.java
                    )*/

                    /*val result : String = "∙ Country code : ${mDataDecode.countryCode.value}\n" +
                                "∙ Merchant Name : ${mDataDecode.merchantName.value}\n" +
                            "∙ TransactionAmount : ${mDataDecode.transactionAmount.value}" */

                    MessageUtils.initAlertDialog(
                        this@ScanQrByServiceGoogleActivity,
                        Util.getEncodeTag(scannedValue)) {
                        finish()
                    }

                } else {
                    MessageUtils.alertErrorConfirm(this, "ALERT", scannedValue) {
                        finish()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()

        /*detector.release()
        cameraSource.release()*/
    }

}