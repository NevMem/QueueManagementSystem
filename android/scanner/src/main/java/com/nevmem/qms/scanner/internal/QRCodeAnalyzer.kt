package com.nevmem.qms.scanner.internal

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

internal class QRCodeAnalyzer(private val onSuccess: (List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        // val rotation = image.imageInfo.rotationDegrees.toMLKitRotation()
        image.image?.let {
            val imageValue = InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(imageValue)
                .addOnCompleteListener { barcodes ->
                    onSuccess(barcodes.result)
                    image.image?.close()
                    image.close()
                }
                .addOnFailureListener { failure ->
                    failure.printStackTrace()
                    image.image?.close()
                    image.close()
                }
        }
    }

    private fun Int.toMLKitRotation(): Int {
        return when (this) {
            0 -> 0
            90 -> 1
            180 -> 2
            270 -> 3
            else -> throw IllegalArgumentException("Not supported rotation degrees")
        }
    }
}
