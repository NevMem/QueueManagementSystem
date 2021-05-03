package com.nevmem.qms.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class SimpleDialogFragment : BottomSheetDialogFragment() {

    lateinit var onOk: () -> Unit
    lateinit var onCancel: () -> Unit
    lateinit var message: String

    private val okButton by lazy {
        requireView().findViewById<MaterialButton>(R.id.okButton)
    }

    private val cancelButton by lazy {
        requireView().findViewById<MaterialButton>(R.id.cancelButton)
    }

    private val dialogMessage by lazy {
        requireView().findViewById<AppCompatTextView>(R.id.dialogMessage)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_simple_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        okButton.setOnClickListener {
            onOk()
            dismiss()
        }

        cancelButton.setOnClickListener {
            onCancel()
            dismiss()
        }

        dialogMessage.text = message
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onCancel()
    }

    companion object {
        fun newInstance(): SimpleDialogFragment = SimpleDialogFragment()
    }
}
