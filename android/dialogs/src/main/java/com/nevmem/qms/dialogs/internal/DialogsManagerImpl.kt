package com.nevmem.qms.dialogs.internal

import androidx.fragment.app.FragmentManager
import com.nevmem.qms.common.operations.OperationStatus
import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.dialogs.FragmentManagerProvider
import com.nevmem.qms.dialogs.fragments.OperationStatusDialog
import com.nevmem.qms.dialogs.fragments.OptionsDialog
import com.nevmem.qms.dialogs.fragments.SimpleDialogFragment
import com.nevmem.qms.dialogs.fragments.TextInputDialog
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.suspendCoroutine

internal class DialogsManagerImpl : DialogsManager {

    override var fragmentManagerProvider: FragmentManagerProvider? = null

    private val fragmentManager: FragmentManager
        get() {
            val provider = fragmentManagerProvider
            check(provider != null)
            return provider.provideFragmentManager()
        }

    override suspend fun showSimpleDialog(message: String): DialogsManager.SimpleDialogResolution
            = suspendCoroutine { continuation ->
        val fragment = SimpleDialogFragment.newInstance()
        fragment.message = message
        fragment.onCancel = {
            continuation.resumeWith(Result.success(DialogsManager.SimpleDialogResolution.Cancel))
        }
        fragment.onOk = {
            continuation.resumeWith(Result.success(DialogsManager.SimpleDialogResolution.Ok))
        }
        fragmentManager.let {
            fragment.show(it, "simple-dialog")
        }
    }

    override suspend fun showTextInputDialog(
        message: String,
        inputLabel: String
    ): DialogsManager.TextInputDialogResolution = suspendCoroutine { continuation ->
        val fragment = TextInputDialog.newInstance()
        fragment.message = message
        fragment.inputLabel = inputLabel
        fragment.onDismiss = {
            continuation.resumeWith(
                Result.success(DialogsManager.TextInputDialogResolution.Dismissed))
        }
        fragment.onText = { text ->
            continuation.resumeWith(
                Result.success(DialogsManager.TextInputDialogResolution.Text(text)))
        }
        fragmentManager.let { fragment.show(it, "text-input-dialog") }
    }

    override fun showOperationStatusDialog(status: Flow<OperationStatus<*>>) {
        val fragment = OperationStatusDialog.newInstance()
        fragment.operationStatus = status
        fragmentManager.let {
            fragment.show(it, "operation-status")
        }
    }

    override suspend fun <T> showOptions(
        message: String,
        options: List<DialogsManager.OptionItem<T>>
    ): DialogsManager.OptionsResolution<T> = suspendCoroutine { continuation ->
        val fragment = OptionsDialog.newInstance()
        fragment.options = options
        fragment.onDismiss = {
            continuation.resumeWith(Result.success(
                DialogsManager.OptionsResolution.Dismissed()))
        }
        fragment.onResult = {
            continuation.resumeWith(Result.success(
                DialogsManager.OptionsResolution.Result(it.value as T)))
        }
        fragment.message = message
        fragmentManager.let {
            fragment.show(it, "options-dialog")
        }
    }
}
