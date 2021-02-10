package com.nevmem.qms.notifications.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.nevmem.qms.notifications.Channel
import com.nevmem.qms.notifications.Notification
import com.nevmem.qms.notifications.NotificationsManager

internal class NotificationsManagerImpl(
    private val context: Context
) : NotificationsManager {
    private val idForChannel = mutableMapOf<String, Int>()
    private val isChannelRegistered = mutableSetOf<String>()

    override fun showNotificationInChannel(
        channel: String,
        notification: Notification,
        priority: Int
    ) {
        val builder = NotificationCompat.Builder(context, channel)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setSmallIcon(notification.iconResourceId)

        val id = nextIdForChannel(channel)
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }

    override fun registerChannelIfNeeded(channel: Channel) {
        if (channel.id in isChannelRegistered) {
            return
        }
        isChannelRegistered += channel.id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel.channelName
            val descriptionText = channel.channelDescription
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channel.id, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun nextIdForChannel(channel: String): Int {
        if (channel in idForChannel) {
            idForChannel[channel] = idForChannel[channel]!! + 1
        } else {
            idForChannel[channel] = 0
        }
        return idForChannel[channel]!!
    }
}
