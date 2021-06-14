package com.nevmem.qms.onboarding.internal.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nevmem.qms.onboarding.R
import com.nevmem.qms.onboarding.internal.data.OnboardingItem

class OnboardingDialogFragment : BottomSheetDialogFragment() {

    private val textView by lazy {
        requireView().findViewById<MaterialTextView>(R.id.text)
    }

    private val buttonView by lazy {
        requireView().findViewById<MaterialButton>(R.id.okButton)
    }

    private lateinit var item: OnboardingItem
    private lateinit var onClose: (OnboardingItem, Boolean) -> Unit

    private var willBeDismissed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_oboarding_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView.text = item.text
        buttonView.setOnClickListener {
            willBeDismissed = true
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onClose(item, willBeDismissed)
    }

    companion object {
        internal fun newInstance(item: OnboardingItem, onClose: (OnboardingItem, Boolean) -> Unit): OnboardingDialogFragment {
            val fragment = OnboardingDialogFragment()
            fragment.item = item
            fragment.onClose = onClose
            return fragment
        }
    }
}
