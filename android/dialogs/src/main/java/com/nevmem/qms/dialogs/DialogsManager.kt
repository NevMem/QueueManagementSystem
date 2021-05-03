package com.nevmem.qms.dialogs

interface DialogsManager {
    var fragmentManagerProvider: FragmentManagerProvider?

    sealed class SimpleDialogResolution {
        object Ok : SimpleDialogResolution()
        object Cancel : SimpleDialogResolution()
    }
    suspend fun showSimpleDialog(message: String): SimpleDialogResolution
}
