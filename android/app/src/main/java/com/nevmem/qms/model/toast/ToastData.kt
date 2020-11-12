package com.nevmem.qms.model.toast

enum class Type {
    Success, Default, Error
}

data class ToastData(
    val type: Type,
    val message: String)
