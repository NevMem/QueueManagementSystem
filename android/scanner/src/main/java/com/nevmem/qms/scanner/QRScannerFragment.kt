package com.nevmem.qms.scanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.barcode.Barcode
import com.nevmem.qms.permissions.Permission
import com.nevmem.qms.permissions.PermissionsManager
import com.nevmem.qms.scanner.internal.QRCodeAnalyzer
import com.nevmem.qms.scanner.internal.parser.JsonParser
import com.nevmem.qms.scanner.internal.parser.SimpleParser


class QRScannerFragment : BottomSheetDialogFragment() {

    enum class State {
        NONE,
        START,
        ASKING_FOR_PERMISSION,
        SCANNING,
        PERMISSION_DENIED,
        PERMISSION_DENIED_OUTSIDE_APP_RETRY,
    }

    private val parsers = listOf(
        SimpleParser(),
        JsonParser()
    )

    private var state: State = State.NONE
        set(value) {
            if (field == value) {
                return
            }
            field = value
            processState()
        }

    lateinit var permissionsManager: PermissionsManager
    lateinit var onFound: (String) -> Unit

    private lateinit var textView: AppCompatTextView
    private lateinit var iconView: AppCompatImageView
    private lateinit var actionButton: MaterialButton
    private lateinit var preview: PreviewView
    private lateinit var previewHeader: AppCompatTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconView = view.findViewById(R.id.fragmentIcon)
        textView = view.findViewById(R.id.fragmentText)
        actionButton = view.findViewById(R.id.actionButton)
        preview = view.findViewById(R.id.preview)
        previewHeader = view.findViewById(R.id.previewHeader)

        state = State.START
    }

    override fun onResume() {
        super.onResume()
        if (state == State.PERMISSION_DENIED_OUTSIDE_APP_RETRY && permissionsManager.hasPermission(Permission.CAMERA)) {
            state = State.SCANNING
        }
    }

    private fun processState() {
        when (state) {
            State.START -> processStartState()
            State.ASKING_FOR_PERMISSION -> processAskingForPermission()
            State.PERMISSION_DENIED -> processPermissionDenied()
            State.PERMISSION_DENIED_OUTSIDE_APP_RETRY -> processPermissionDeniedAndCannotRetry()
            State.SCANNING -> processScanning()
            else -> {}
        }
    }

    private fun processStartState() {
        state = if (!permissionsManager.hasPermission(Permission.CAMERA)) {
            State.ASKING_FOR_PERMISSION
        } else {
            State.SCANNING
        }
    }

    private fun processAskingForPermission() {
        iconView.apply { setImageResource(R.drawable.icon_camera) }
        textView.apply { setText(R.string.ask_for_permissions) }
        actionButton.apply {
            setText(R.string.action_button_grant)
            setOnClickListener {
                permissionsManager.requestPermissions(listOf(Permission.CAMERA)) { response ->
                    if (state == State.ASKING_FOR_PERMISSION) {
                        state = if (response.isAllGranted) {
                            State.SCANNING
                        } else if (!response.cannotRetry) {
                            State.PERMISSION_DENIED
                        } else {
                            State.PERMISSION_DENIED_OUTSIDE_APP_RETRY
                        }
                    }
                }
            }
        }
    }

    private fun processPermissionDenied() {
        iconView.apply { setImageResource(R.drawable.icon_sadface) }
        textView.apply { setText(R.string.not_granted_and_ask_for_permissions) }
        actionButton.apply {
            setText(R.string.action_button_grant)
            setOnClickListener {
                permissionsManager.requestPermissions(listOf(Permission.CAMERA)) { response ->
                    if (state == State.PERMISSION_DENIED) {
                        state = if (response.isAllGranted) {
                            State.SCANNING
                        } else if (!response.cannotRetry) {
                            State.PERMISSION_DENIED
                        } else {
                            State.PERMISSION_DENIED_OUTSIDE_APP_RETRY
                        }
                    }
                }
            }
        }
    }

    private fun processPermissionDeniedAndCannotRetry() {
        iconView.apply { setImageResource(R.drawable.icon_sadface) }
        textView.apply { setText(R.string.not_granted_cannot_retry) }
        actionButton.apply {
            setText(R.string.action_button_grant_in_settings)
            setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun processScanning() {
        iconView.isVisible = false
        textView.isVisible = false
        actionButton.isVisible = false

        preview.isVisible = true
        previewHeader.isVisible = true
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val executor = ContextCompat.getMainExecutor(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview.createSurfaceProvider())
                }

            val analyzer = ImageAnalysis.Builder()
                .build().also { it.setAnalyzer(executor, QRCodeAnalyzer { barcodes ->
                    processBarcodes(barcodes)
                }) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, analyzer)
            } catch (exception: Exception) {}
        }, executor)
    }

    private fun processBarcodes(barcodes: List<Barcode>) {
        barcodes.forEach {
            val rawValue = it.rawValue
            if (rawValue != null) {
                parsers.forEach { parser ->
                    parser.parse(rawValue)?.let { value -> onFound(value) }
                }
            }
        }
    }

    companion object {
        fun newInstance(): QRScannerFragment = QRScannerFragment()
    }
}
