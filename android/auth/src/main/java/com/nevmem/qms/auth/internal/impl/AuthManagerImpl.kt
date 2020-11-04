package com.nevmem.qms.auth.internal.impl

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.*
import kotlinx.coroutines.channels.Channel

internal class AuthManagerImpl : AuthManager {
    override val token: String
        get() = TODO("Not yet implemented")
    override val authenticationStatus: Channel<AuthenticationStatus>
        get() = TODO("Not yet implemented")

    override fun login(credentials: LoginCredentials): Channel<LoginState> {
        TODO("Not yet implemented")
    }

    override fun register(credentials: RegisterCredentials): Channel<RegisterState> {
        TODO("Not yet implemented")
    }
}
