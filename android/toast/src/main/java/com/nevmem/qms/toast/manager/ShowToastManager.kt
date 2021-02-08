package com.nevmem.qms.toast.manager

interface ShowToastManager {
    fun showToast(message: String, type: Type)

    fun error(message: String)
    fun default(message: String)
    fun success(message: String)
}
