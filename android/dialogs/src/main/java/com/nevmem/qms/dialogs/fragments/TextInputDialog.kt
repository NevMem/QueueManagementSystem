package com.nevmem.qms.dialogs.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nevmem.qms.dialogs.R

class TextInputDialog : BottomSheetDialogFragment() {

    lateinit var onText: (String) -> Unit
    lateinit var onDismiss: () -> Unit
    lateinit var message: String
    lateinit var inputLabel: String

    private var willBeDismissed = false

    private val okButton by lazy { requireView().findViewById<MaterialButton>(R.id.okButton) }
    private val cancelButton by lazy {
        requireView().findViewById<MaterialButton>(R.id.cancelButton)
    }
    private val textField by lazy {
        requireView().findViewById<TextInputEditText>(R.id.textInput)
    }
    private val dialogMessage by lazy {
        requireView().findViewById<AppCompatTextView>(R.id.dialogMessage)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_text_input_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogMessage.text = message
        textField.hint = inputLabel

        okButton.setOnClickListener {
            willBeDismissed = true
            onText(textField.text.toString())
            dismiss()
        }

        cancelButton.setOnClickListener {
            willBeDismissed = true
            onDismiss()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!willBeDismissed) {
            onDismiss()
        }
    }

    companion object {
        fun newInstance(): TextInputDialog = TextInputDialog()
    }
}
