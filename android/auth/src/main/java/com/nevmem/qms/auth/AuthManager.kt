package com.nevmem.qms.auth

import com.nevmem.qms.auth.data.*
import kotlinx.coroutines.channels.Channel

interface AuthManager {
    val token: String
    val authenticationStatus: Channel<AuthenticationStatus>

    fun login(credentials: LoginCredentials): Channel<LoginState>
    fun register(credentials: RegisterCredentials): Channel<RegisterState>
}
