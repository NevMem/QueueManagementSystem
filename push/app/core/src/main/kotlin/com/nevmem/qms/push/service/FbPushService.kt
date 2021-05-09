package com.nevmem.qms.push.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.nevmem.qms.push.data.NotificationConfig
import org.springframework.stereotype.Service
import java.io.FileInputStream

@Service
class FbPushService {
    init {
        val serviceAccount = FileInputStream(
            "${System.getenv("PUSH_WORKING_DIR")}/nevmem-qms-firebase-adminsdk-jq1o6-e98d0d8f16.json")

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun broadcast(tokens: List<String>, data: Map<String, String>, notification: NotificationConfig?) {
        val builder = MulticastMessage.builder()
            .putAllData(data)
            .addAllTokens(tokens)

        if (notification != null) {
            builder.setNotification(Notification.builder()
                .setTitle(notification.title)
                .setBody(notification.body)
                .build())
        }

        FirebaseMessaging.getInstance().sendMulticast(builder.build())
    }

    fun sendToOne(token: String, data: Map<String, String>, notification: NotificationConfig?) {
        val builder = Message.builder()
            .putAllData(data)
            .setToken(token)

        if (notification != null) {
            builder.setNotification(Notification.builder()
                .setTitle(notification.title)
                .setBody(notification.body)
                .build())
        }

        FirebaseMessaging.getInstance().send(builder.build())
    }
}
