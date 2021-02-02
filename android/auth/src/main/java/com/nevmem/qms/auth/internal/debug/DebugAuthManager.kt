package com.nevmem.qms.auth.internal.debug

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.*
import com.nevmem.qms.keyvalue.KeyValueStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private const val AUTH_TOKEN_STORAGE_KEY = "authentication-token"

internal class DebugAuthManager(
    private val storage: KeyValueStorage
) : AuthManager {
    override var token: String = ""
    private var attempt = 0

    override val authenticationStatus: Channel<AuthenticationStatus>
        get() = Channel<AuthenticationStatus>(Channel.UNLIMITED).also { channel ->
                GlobalScope.launch {
                    channel.send(AuthenticationStatus.Pending)
                    delay(750)
                    if (storage.hasKey(AUTH_TOKEN_STORAGE_KEY)) {
                        channel.send(AuthenticationStatus.LoggedIn)
                    } else {
                        channel.send(AuthenticationStatus.Unauthorized)
                    }
                    channel.close()
                }
            }

    override fun login(credentials: LoginCredentials): Flow<LoginState> = flow {
            emit(LoginState.Processing)
            delay(500)
            if (credentials.login == credentials.password) {
                storage.setValue(AUTH_TOKEN_STORAGE_KEY, credentials.login)
                emit(LoginState.Success)
            } else {
                emit(LoginState.Error("Login or password is incorrect ${attempt + 1}"))
                attempt += 1
            }
        }

    override fun register(credentials: RegisterCredentials): Flow<RegisterState> = flow {
            GlobalScope.launch {
                emit(RegisterState.Processing)
                delay(3000)
                emit(RegisterState.Success)
            }
        }
}
