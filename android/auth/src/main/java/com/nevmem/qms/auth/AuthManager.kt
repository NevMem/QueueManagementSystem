package com.nevmem.qms.auth

import com.nevmem.qms.ClientApiProto
import com.nevmem.qms.auth.data.*
import com.nevmem.qms.common.operations.OperationStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface AuthManager {
    val token: String
    val authenticationStatus: Channel<AuthenticationStatus>

    fun currentUser(): Flow<OperationStatus<ClientApiProto.User>>

    fun login(credentials: LoginCredentials): Flow<LoginState>
    fun register(credentials: RegisterCredentials): Flow<RegisterState>
    fun logout()
}
