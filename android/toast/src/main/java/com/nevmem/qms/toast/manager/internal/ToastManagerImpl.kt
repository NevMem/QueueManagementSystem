package com.nevmem.qms.toast.manager.internal

import com.nevmem.qms.toast.manager.ToastData
import com.nevmem.qms.toast.manager.ToastManager
import com.nevmem.qms.toast.manager.ToastProvider
import com.nevmem.qms.toast.manager.Type
import java.util.*

internal class ToastManagerImpl : ToastManager {

    private val toastQueue = LinkedList<ToastData>()

    private val listeners = mutableSetOf<ToastProvider.Listener>()

    override val hasToast: Boolean get() = toastQueue.isNotEmpty()

    override fun consumeOneToast(): ToastData = toastQueue.removeFirst()

    override fun showToast(message: String, type: Type) {
        toastQueue.add(ToastData(type, message))
        listeners.forEach { it.onHasToastsChanged() }
    }

    override fun addListener(listener: ToastProvider.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: ToastProvider.Listener) {
        listeners.remove(listener)
    }
}
