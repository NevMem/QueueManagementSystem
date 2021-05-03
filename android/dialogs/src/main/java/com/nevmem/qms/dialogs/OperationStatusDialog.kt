package com.nevmem.qms.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nevmem.qms.common.operations.OperationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OperationStatusDialog : BottomSheetDialogFragment() {

    lateinit var operationStatus: Flow<OperationStatus<*>>

    private val progressBar by lazy {
        requireView().findViewById<ProgressBar>(R.id.progressBar)
    }

    private val successImage by lazy {
        requireView().findViewById<AppCompatImageView>(R.id.successIcon)
    }

    private val errorMessage by lazy {
        requireView().findViewById<AppCompatTextView>(R.id.errorString)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_operation_status_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            operationStatus.collect { status ->
                listOf(progressBar, successImage, errorMessage).forEach { it.isVisible = false }
                when (status) {
                    is OperationStatus.Pending -> progressBar.isVisible = true
                    is OperationStatus.Error -> {
                        errorMessage.isVisible = true
                        errorMessage.text = status.message
                    }
                    is OperationStatus.Success -> successImage.isVisible = true
                }
            }
        }
    }

    companion object {
        fun newInstance(): OperationStatusDialog = OperationStatusDialog()
    }
}
