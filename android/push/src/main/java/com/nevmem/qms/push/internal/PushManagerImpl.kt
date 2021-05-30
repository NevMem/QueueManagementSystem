package com.nevmem.qms.push.internal

import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.messaging.FirebaseMessaging
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.auth.data.AuthenticationStatus
import com.nevmem.qms.common.NEW_PUSH_BROADCAST
import com.nevmem.qms.common.NEW_PUSH_TOKEN_BROADCAST
import com.nevmem.qms.common.utils.infiniteRetry
import com.nevmem.qms.common.utils.runOnIO
import com.nevmem.qms.data.NewPushTokenRequest
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.push.PushManager
import com.nevmem.qms.push.PushProcessor
import com.nevmem.qms.push.broadcast.PushBroadcastReceiver
import kotlinx.coroutines.channels.consume

internal class PushManagerImpl(
    lifecycleOwner: LifecycleOwner,
    private val networkManager: NetworkManager,
    private val authManager: AuthManager,
    private val context: Context,
    private val logger: Logger
) : PushManager, LifecycleObserver {

    private val processors = mutableSetOf<PushProcessor>()

    private val receiver = PushBroadcastReceiver(logger, ::onPushData, ::onNewToken)

    init {
        lifecycleOwner.lifecycle.addObserver(this)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            onNewToken(token)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        context.registerReceiver(receiver, IntentFilter().apply {
            addAction(NEW_PUSH_BROADCAST)
            addAction(NEW_PUSH_TOKEN_BROADCAST)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        context.unregisterReceiver(receiver)
    }

    override fun addPushProcessor(processor: PushProcessor) {
        processors += processor
    }

    override fun removePushProcessor(processor: PushProcessor) {
        processors -= processor
    }

    override fun processDataFromIntent(data: Map<String, String>) {
        logger.reportEvent("push-manager.data-from-intent", data)
        onPushData(data)
    }

    private fun onPushData(data: Map<String, String>) {
        logger.reportEvent("push-manager.data-from-receiver", data)
        processors.forEach { processor -> processor.onPushData(data) }
    }

    private fun onNewToken(token: String) {
        logger.reportEvent("push-manager.new-token", mapOf("token" to token))
        runOnIO {
            for (status in authManager.authenticationStatus) {
                if (status is AuthenticationStatus.LoggedIn) {
                    infiniteRetry {
                        networkManager.registerNewPushToken(NewPushTokenRequest(token), authManager.token)
                    }
                }
            }
        }
    }
}
