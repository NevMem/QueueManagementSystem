package com.nevmem.qms.auth.data

sealed class RegisterState {
    object Processing : RegisterState()
    class Error(val error: String) : RegisterState()
    object Success : RegisterState()
}
