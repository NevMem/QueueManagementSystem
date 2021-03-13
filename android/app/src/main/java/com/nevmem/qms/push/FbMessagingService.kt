package com.nevmem.qms.push

import com.google.firebase.messaging.FirebaseMessagingService

class FbMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
