package com.example.acledaproject.ui.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseActivity
import com.example.acledaproject.databinding.ActivityBarcodeScanningBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private const val CAMERA_PERMISSION_REQUEST_CODE = 1
@ExperimentalGetImage
class DoScanMlKitActivity : BaseActivity() {

  private lateinit var binding: ActivityBarcodeScanningBinding
  private var flashEnabled = false

  companion object {

    val TAG: String = DoScanMlKitActivity::class.java.simpleName
    fun gotoBarcodeScanningActivity(mContext : Context) {
      mContext.startActivity(Intent(mContext, DoScanMlKitActivity::class.java))
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityBarcodeScanningBinding.inflate(layoutInflater)

    setContentView(binding.root)

    initView()

    if (hasCameraPermission()) {
      bindCameraUseCases()
    } else {
      requestPermission()
    }

    binding.overlay.post {
      binding.overlay.setViewFinder()
    }

  }

  private fun initView() {
    binding.toolbar.iconBack.setOnClickListener {
      finish()
    }

    binding.toolbar.titleToolbar.text = "Scan QR"
  }

  // checking to see whether user has already granted permission
  private fun hasCameraPermission() =
    ActivityCompat.checkSelfPermission(
      this,
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

  private fun requestPermission(){
    // opening up dialog to ask for camera permission
    ActivityCompat.requestPermissions(
      this,
      arrayOf(Manifest.permission.CAMERA),
      CAMERA_PERMISSION_REQUEST_CODE
    )
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
      && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // user granted permissions - we can set up our scanner
      bindCameraUseCases()
    } else {
      // user did not grant permissions - we can't use the camera
      Toast.makeText(this,
        "Camera permission required.",
        Toast.LENGTH_LONG
      ).show()
      finish()
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  private fun bindCameraUseCases() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener({
      val cameraProvider = cameraProviderFuture.get()

      // setting up the preview use case
      val previewUseCase = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }

      // configure our MLKit BarcodeScanning client

      /* passing in our desired barcode formats - MLKit supports additional formats outside of the
      ones listed here, and you may not need to offer support for all of these. You should only
      specify the ones you need */

      val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_CODE_128,
        Barcode.FORMAT_CODE_39,
        Barcode.FORMAT_CODE_93,
        Barcode.FORMAT_EAN_8,
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_QR_CODE,
        Barcode.FORMAT_UPC_A,
        Barcode.FORMAT_UPC_E,
        Barcode.FORMAT_PDF417
      ).build()

      // getClient() creates a new instance of the MLKit barcode scanner with the specified options
      val scanner = BarcodeScanning.getClient(options)

      // setting up the analysis use case
      val analysisUseCase = ImageAnalysis.Builder()
        .build()

      // define the actual functionality of our analysis use case
      analysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
          processImageProxy(scanner, imageProxy)
      }

        // configure to use the back camera
      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      try {
        cameraProvider.bindToLifecycle(
          this,
          cameraSelector,
          previewUseCase,
          analysisUseCase)
      } catch (illegalStateException: IllegalStateException) {
        // If the use case has already been bound to another lifecycle or method is not called on main thread.
        Log.e(TAG, illegalStateException.message.orEmpty())
      } catch (illegalArgumentException: IllegalArgumentException) {
        // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
        Log.e(TAG, illegalArgumentException.message.orEmpty())
      }


      // Flash
      val camera =
        cameraProvider?.bindToLifecycle(this, cameraSelector, analysisUseCase, previewUseCase)

      if (camera?.cameraInfo?.hasFlashUnit() == true) {
        binding.ivFlashControl.visibility = View.VISIBLE

        binding.ivFlashControl.setOnClickListener {
          camera.cameraControl.enableTorch(!flashEnabled)
        }

        camera.cameraInfo.torchState.observe(this) {
          it?.let { torchState ->
            if (torchState == TorchState.ON) {
              flashEnabled = true
              binding.ivFlashControl.setImageResource(R.drawable.baseline_flash_on_24)
            } else {
              flashEnabled = false
              binding.ivFlashControl.setImageResource(R.drawable.baseline_flash_off_24)
            }
          }
        }
      }


    }, ContextCompat.getMainExecutor(this))
  }

  private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy
  ) {

    imageProxy.image?.let { image ->
      val inputImage =
        InputImage.fromMediaImage(
          image,
          imageProxy.imageInfo.rotationDegrees
        )

      barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodeList ->
          val barcode = barcodeList.getOrNull(0)

          // `rawValue` is the decoded value of the barcode
          barcode?.rawValue?.let { value ->
            Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
            finish()
          }
        }
        .addOnFailureListener {
          // This failure will happen if the barcode scanning model
          // fails to download from Google Play Services

          Log.e(TAG, it.message.orEmpty())
        }.addOnCompleteListener {
          // When the image is from CameraX analysis use case, must
          // call image.close() on received images when finished
          // using them. Otherwise, new images may not be received
          // or the camera may stall.

          imageProxy.image?.close()

          imageProxy.close()
        }
    }
  }

}