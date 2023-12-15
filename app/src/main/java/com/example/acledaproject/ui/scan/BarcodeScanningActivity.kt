package com.example.acledaproject.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseActivity
import com.example.acledaproject.databinding.ActivityBarcodeScanningBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScanningActivity : BaseActivity() {

    companion object {
        fun gotoBarcodeScanningActivity(mContext : Context) {
            mContext.startActivity(
                Intent(mContext, BarcodeScanningActivity::class.java)
            )
        }
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: ActivityBarcodeScanningBinding

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanningBinding.inflate(layoutInflater)

        setContentView(binding.root)

      /*  var drawable = resources.getDrawable(R.drawable.imge_qr)
        var bitmap = drawableToBitmap(drawable)

        val image = InputImage.fromBitmap(bitmap, 0)

        scanBarcodes(image)
*/
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        checkCameraPermission()


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (e: Exception) {
                Log.e("TAG", "Error setting up camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))


        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), { imageProxy: ImageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                processBarcode(image)
            }
            imageProxy.close()
        })
    }

    private fun processBarcode(image: InputImage) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    val valueType = barcode.valueType

                    // Process the barcode value
                    Log.d("TAG", "Barcode value: $rawValue, Value type: $valueType")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Barcode scanning failed: ${e.message}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }

    private fun checkCameraPermission() {
        try {
            val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
            ActivityCompat.requestPermissions(this, requiredPermissions, 0)
        } catch (e: IllegalArgumentException) {
            checkIfCameraPermissionIsGranted()
        }
    }

    private fun checkIfCameraPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted: start the preview
            startCamera(this)
        } else {
            // Permission denied
            MaterialAlertDialogBuilder(this)
                .setTitle("Permission required")
                .setMessage("This application needs to access the camera to process barcodes")
                .setPositiveButton("Ok") { _, _ ->
                    // Keep asking for permission until granted
                    checkCameraPermission()
                }
                .setCancelable(false)
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                    show()
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        checkIfCameraPermissionIsGranted()
    }

    private fun startCamera(mContext: Context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {

                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer(
                        mContext,
                        BarcodeBoxView(mContext),
                        binding.previewView.width.toFloat(),
                        binding.previewView.height.toFloat()
                    ))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    class QrCodeAnalyzer(
        private val context: Context,
        private val barcodeBoxView: BarcodeBoxView,
        private val previewViewWidth: Float,
        private val previewViewHeight: Float
    ) : ImageAnalysis.Analyzer {

        /**
         * This parameters will handle preview box scaling
         */
        private var scaleX = 1f
        private var scaleY = 1f

        private fun translateX(x: Float) = x * scaleX
        private fun translateY(y: Float) = y * scaleY

        private fun adjustBoundingRect(rect: Rect) = RectF(
            translateX(rect.left.toFloat()),
            translateY(rect.top.toFloat()),
            translateX(rect.right.toFloat()),
            translateY(rect.bottom.toFloat())
        )

        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(image: ImageProxy) {
            val img = image.image
            if (img != null) {
                // Update scale factors
                scaleX = previewViewWidth / img.height.toFloat()
                scaleY = previewViewHeight / img.width.toFloat()

                val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

                // Process image searching for barcodes
                val options = BarcodeScannerOptions.Builder()
                    .build()

                val scanner = BarcodeScanning.getClient(options)

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            for (barcode in barcodes) {
                                // Handle received barcodes...
                                Toast.makeText(
                                    context,
                                    "Value: " + barcode.rawValue,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                // Update bounding rect
                                barcode.boundingBox?.let { rect ->
                                    barcodeBoxView.setRect(
                                        adjustBoundingRect(
                                            rect
                                        )
                                    )
                                }
                            }
                        } else {
                            // Remove bounding rect
                            barcodeBoxView.setRect(RectF())
                        }
                    }
                    .addOnFailureListener { }
            }

            image.close()
        }
    }

    class BarcodeBoxView(
        context: Context
    ) : View(context) {

        private val paint = Paint()

        private var mRect = RectF()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val cornerRadius = 10f

            paint.style = Paint.Style.STROKE
            paint.color = Color.RED
            paint.strokeWidth = 5f

            canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint)
        }

        fun setRect(rect: RectF) {
            mRect = rect
            invalidate()
            requestLayout()
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    private fun scanBarcodes(image: InputImage) {
        // [START set_detector_options]
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC)
            .build()
        // [END set_detector_options]

        // [START get_detector]
        val scanner = BarcodeScanning.getClient()
        // Or, to specify the formats to recognize:
        // val scanner = BarcodeScanning.getClient(options)
        // [END get_detector]

        // [START run_detector]
        val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Task completed successfully
                // [START_EXCLUDE]
                // [START get_barcodes]

                for (barcode in barcodes) {
                    Toast.makeText(this@BarcodeScanningActivity, barcode.rawValue, Toast.LENGTH_SHORT).show()
                    val bounds = barcode.boundingBox
                    val corners = barcode.cornerPoints

                    val rawValue = barcode.rawValue

                    val valueType = barcode.valueType
                    // See API reference for complete list of supported types
                    when (valueType) {
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType
                        }
                        Barcode.TYPE_URL -> {
                            val title = barcode.url!!.title
                            val url = barcode.url!!.url
                        }
                    }
                }
                // [END get_barcodes]
                // [END_EXCLUDE]
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }


        // [END run_detector]
    }

//    class QrCodeAnalyzer : ImageAnalysis.Analyzer {
//
//        @SuppressLint("UnsafeOptInUsageError")
//        override fun analyze(image: ImageProxy) {
//            val img = image.image
//            if (img != null) {
//                val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)
//
//                // Process image searching for barcodes
//                val options = BarcodeScannerOptions.Builder()
//                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//                    .build()
//
//                val scanner = BarcodeScanning.getClient(options)
//
//                scanner.process(inputImage)
//                    .addOnSuccessListener { barcodes ->
//                        for (barcode in barcodes) {
//                            val rawValue = barcode.rawValue
//                            Log.d("jeeeeeeeeeeeeeee", rawValue.toString())
//                        }
//                    }
//                    .addOnFailureListener { }
//            }
//
//            image.close()
//        }
//    }
}