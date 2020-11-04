package com.nevmem.qms.auth.data

sealed class LoginState {
    object Processing : LoginState()
    class Error(val error: String) : LoginState()
    object Success : LoginState()
}
