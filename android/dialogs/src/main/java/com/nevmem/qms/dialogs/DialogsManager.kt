package com.nevmem.qms.dialogs

import com.nevmem.qms.common.operations.OperationStatus
import kotlinx.coroutines.flow.Flow

interface DialogsManager {
    var fragmentManagerProvider: FragmentManagerProvider?

    sealed class SimpleDialogResolution {
        object Ok : SimpleDialogResolution()
        object Cancel : SimpleDialogResolution()
    }
    suspend fun showSimpleDialog(message: String): SimpleDialogResolution

    sealed class TextInputDialogResolution {
        object Dismissed : TextInputDialogResolution()
        data class Text(val text: String) : TextInputDialogResolution()
    }
    suspend fun showTextInputDialog(
        message: String, inputLabel: String
    ) : TextInputDialogResolution

    fun showOperationStatusDialog(status: Flow<OperationStatus<*>>)
}
