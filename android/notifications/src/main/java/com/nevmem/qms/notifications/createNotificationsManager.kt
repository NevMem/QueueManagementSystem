package com.nevmem.qms.notifications

import android.content.Context
import com.nevmem.qms.notifications.internal.NotificationsManagerImpl

fun createNotificationsManager(context: Context): NotificationsManager = NotificationsManagerImpl(context)
