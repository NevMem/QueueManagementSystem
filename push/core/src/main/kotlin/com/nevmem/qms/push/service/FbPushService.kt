package com.nevmem.qms.push.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import org.springframework.stereotype.Service
import java.io.FileInputStream


@Service
class FbPushService {
    init {
        val serviceAccount = FileInputStream(
            "${System.getenv("PUSH_WORKING_DIR")}/nevmem-qms-firebase-adminsdk-jq1o6-e98d0d8f16.json")

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun broadcast(tokens: List<String>, messageString: String) {
        val message = MulticastMessage.builder()
            .putData("message", messageString)
            .addAllTokens(tokens)
            .build()

        val response = FirebaseMessaging.getInstance().sendMulticast(message)
    }
}
