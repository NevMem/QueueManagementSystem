package com.nevmem.qms.auth.internal.impl

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

internal class AuthManagerImpl : AuthManager {
    override val token: String
        get() = TODO("Not yet implemented")
    override val authenticationStatus: Channel<AuthenticationStatus>
        get() = TODO("Not yet implemented")

    override fun login(credentials: LoginCredentials): Flow<LoginState> {
        TODO("Not yet implemented")
    }

    override fun register(credentials: RegisterCredentials): Flow<RegisterState> {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }
}
