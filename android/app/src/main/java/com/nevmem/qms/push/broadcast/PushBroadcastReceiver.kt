package com.nevmem.qms.push.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nevmem.qms.common.PUSH_BROADCAST
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.utils.runOnUi

class PushBroadcastReceiver(
    private val logger: Logger,
    private val listener: (Map<String, String>) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        logger.reportEvent("push-broadcast-receiver", mapOf(
            "type" to "got-intent"
        ))
        intent?.let {
            if (it.action == PUSH_BROADCAST) {
                val payload = it.getStringExtra("payload")
                if (payload == null) {
                    logError("null payload")
                    return
                }
                val data = payload.split(",").map { keyValue ->
                    val split = keyValue.split("=")
                    if (split.size != 2) {
                        logError("bad param: $keyValue")
                        return
                    }
                    split[0] to split[1]
                }.toMap()
                runOnUi { listener(data) }
            }
        }
    }

    private fun logError(error: String) {
        logger.reportEvent("push-broadcast-receiver", mapOf(
            "type" to "error",
            "message" to error
        ))
    }
}
