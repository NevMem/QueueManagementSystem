package com.nevmem.qms.push

import com.nevmem.qms.toast.manager.ShowToastManager

class ToastPushProcessor(
    private val showToastManager: ShowToastManager
): PushProcessor {
    override fun onPushData(data: Map<String, String>) {
        if (data.containsKey("message")) {
            showToastManager.default(data.getValue("message"))
        }
    }
}
