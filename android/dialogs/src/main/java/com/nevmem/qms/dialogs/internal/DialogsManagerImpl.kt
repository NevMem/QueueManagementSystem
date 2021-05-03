package com.nevmem.qms.dialogs.internal

import androidx.fragment.app.FragmentManager
import com.nevmem.qms.dialogs.DialogsManager
import com.nevmem.qms.dialogs.FragmentManagerProvider
import com.nevmem.qms.dialogs.SimpleDialogFragment
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
}
