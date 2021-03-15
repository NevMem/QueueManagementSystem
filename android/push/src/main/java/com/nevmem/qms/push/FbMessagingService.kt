package com.nevmem.qms.push

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nevmem.qms.common.PUSH_BROADCAST

class FbMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
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
        sendBroadcast(Intent(PUSH_BROADCAST).apply {
            putExtra("payload", data.map { "${it.key}=${it.value}" }.joinToString(","))
        })
    }
}
