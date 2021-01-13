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
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.nevmem.qms.permissions.Permission
import com.nevmem.qms.permissions.PermissionsManager


class QRScannerFragment : BottomSheetDialogFragment() {

    enum class State {
        NONE,
        START,
        ASKING_FOR_PERMISSION,
        SCANNING,
        PERMISSION_DENIED,
        PERMISSION_DENIED_OUTSIDE_APP_RETRY,
    }

    private var state: State = State.NONE
        set(value) {
            if (field == value) {
                return
            }
            field = value
            processState()
        }

    lateinit var permissionsManager: PermissionsManager

    private lateinit var textView: AppCompatTextView
    private lateinit var iconView: AppCompatImageView
    private lateinit var actionButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconView = view.findViewById<AppCompatImageView>(R.id.fragmentIcon)
        textView = view.findViewById<AppCompatTextView>(R.id.fragmentText)
        actionButton = view.findViewById<MaterialButton>(R.id.actionButton)

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
    }

    companion object {
        fun newInstance(): QRScannerFragment = QRScannerFragment()
    }
}
