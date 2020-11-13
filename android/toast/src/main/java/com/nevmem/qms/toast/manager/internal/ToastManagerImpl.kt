package com.nevmem.qms.toast.manager.internal

import com.nevmem.qms.toast.manager.ToastData
import com.nevmem.qms.toast.manager.ToastManager
import com.nevmem.qms.toast.manager.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

internal class ToastManagerImpl : ToastManager {
    private val channel = Channel<ToastData>(Channel.UNLIMITED)

    override val toastFlow: Flow<ToastData> = channel.consumeAsFlow()

    override fun showToast(message: String, type: Type) {
        GlobalScope.launch(Dispatchers.Main) {
            channel.send(ToastData(type, message))
        }
    }
}
