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

    data class OptionItem<T>(val iconId: Int, val name: String, val value: T)
    sealed class OptionsResolution<T> {
        class Dismissed<T> : OptionsResolution<T>()
        data class Result<T>(val value: T) : OptionsResolution<T>()
    }
    suspend fun<T> showOptions(message: String, options: List<OptionItem<T>>): OptionsResolution<T>

    fun showOperationStatusDialog(status: Flow<OperationStatus<*>>)
}
