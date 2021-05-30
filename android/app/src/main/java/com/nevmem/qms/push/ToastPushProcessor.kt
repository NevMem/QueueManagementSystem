package com.nevmem.qms.push

import com.nevmem.qms.toast.manager.ShowToastManager

class ToastPushProcessor(
    private val showToastManager: ShowToastManager
): PushProcessor {
    override fun onPushData(data: Map<String, String>) {
        println("cur_deb $data")
        if (data.containsKey("message")) {
            val message = data.getValue("message")
            when (data["type"]) {
                "error" -> showToastManager.error(message)
                "success" -> showToastManager.success(message)
                else -> showToastManager.default(data.getValue("message"))
            }
        }
    }
}
