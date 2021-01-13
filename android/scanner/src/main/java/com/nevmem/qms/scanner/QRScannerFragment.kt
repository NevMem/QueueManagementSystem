package com.nevmem.qms.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class QRScannerFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatImageView>(R.id.fragmentIcon).apply {
            setImageResource(R.drawable.icon_camera)
        }

        view.findViewById<AppCompatTextView>(R.id.fragmentText).apply {
            setText(R.string.ask_for_permissions)
        }

        view.findViewById<MaterialButton>(R.id.actionButton).apply {
            setText(R.string.action_button_grant)
        }
    }

    companion object {
        fun newInstance(): QRScannerFragment = QRScannerFragment()
    }
}
