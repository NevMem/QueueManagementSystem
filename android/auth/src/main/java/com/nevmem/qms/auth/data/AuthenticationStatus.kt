package com.nevmem.qms.auth.data

sealed class AuthenticationStatus {
    object Pending : AuthenticationStatus()
    object LoggedIn : AuthenticationStatus()
    object Unauthorized : AuthenticationStatus()
}
