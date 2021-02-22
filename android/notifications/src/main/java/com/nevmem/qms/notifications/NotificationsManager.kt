package com.nevmem.qms.notifications

import androidx.core.app.NotificationCompat

interface NotificationsManager {
    fun registerChannelIfNeeded(channel: Channel)
    fun showNotificationInChannel(
        channel: String,
        notification: Notification,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT)
}
