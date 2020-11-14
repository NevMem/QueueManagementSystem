package com.nevmem.qms.auth

import com.nevmem.qms.auth.data.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface AuthManager {
    val token: String
    val authenticationStatus: Channel<AuthenticationStatus>

    fun login(credentials: LoginCredentials): Flow<LoginState>
    fun register(credentials: RegisterCredentials): Channel<RegisterState>
}
