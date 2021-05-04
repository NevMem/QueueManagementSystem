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

    fun showOperationStatusDialog(status: Flow<OperationStatus<*>>)
}
