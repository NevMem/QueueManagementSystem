package com.nevmem.qms.auth.internal.debug

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DebugAuthManager : AuthManager {
    override var token: String = ""

    override val authenticationStatus: Channel<AuthenticationStatus>
        get() = Channel<AuthenticationStatus>(Channel.UNLIMITED).also { channel ->
                GlobalScope.launch {
                    channel.send(AuthenticationStatus.Pending)
                    delay(750)
                    channel.send(AuthenticationStatus.Unauthorized)
                    channel.close()
                }
            }

    override fun login(credentials: LoginCredentials): Channel<LoginState> =
        Channel<LoginState>(Channel.UNLIMITED).also { channel ->
            GlobalScope.launch {
                channel.send(LoginState.Processing)
                delay(3000)
                if (credentials.login == credentials.password) {
                    channel.send(LoginState.Success)
                } else {
                    channel.send(LoginState.Error("Login or password is incorrect"))
                }
                channel.close()
            }
        }

    override fun register(credentials: RegisterCredentials): Channel<RegisterState> =
        Channel<RegisterState>(Channel.UNLIMITED).also { channel ->
            GlobalScope.launch {
                channel.send(RegisterState.Processing)
                delay(3000)
                channel.send(RegisterState.Success)
                channel.close()
            }
        }
}
