package com.nevmem.qms.toast.manager

interface ToastProvider {
    interface Listener {
        fun onHasToastsChanged()
    }

    val hasToast: Boolean

    fun consumeOneToast(): ToastData

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)
}
