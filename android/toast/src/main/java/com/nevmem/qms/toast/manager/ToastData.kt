package com.nevmem.qms.toast.manager

enum class Type {
    Success, Default, Error
}

data class ToastData(
    val type: Type,
    val message: String)
