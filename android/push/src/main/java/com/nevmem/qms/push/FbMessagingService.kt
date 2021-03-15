package com.nevmem.qms.push

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nevmem.qms.common.NEW_PUSH_BROADCAST
import com.nevmem.qms.common.NEW_PUSH_TOKEN_BROADCAST

class FbMessagingService : FirebaseMessagingService() {

    init {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            gotToken(token)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        gotToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.data.let { data ->
            if (data.isNotEmpty()) {
                sendToBroadcastReceiver(data)
            }
        }
    }

    private fun sendToBroadcastReceiver(data: Map<String, String>) {
        sendBroadcast(Intent(NEW_PUSH_BROADCAST).apply {
            putExtra("payload", data.map { "${it.key}=${it.value}" }.joinToString(","))
        })
    }

    private fun gotToken(token: String) {
        sendBroadcast(Intent(NEW_PUSH_TOKEN_BROADCAST).apply {
            putExtra("token", token)
        })
    }
}
