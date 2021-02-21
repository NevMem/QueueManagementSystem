package com.nevmem.qms.status

import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.notifications.NotificationsManager
import com.nevmem.qms.status.internal.DebugStatusProviderImpl
import com.nevmem.qms.status.internal.NetworkStatusProvider


fun createDebugStatusProvider(
    networkManager: NetworkManager,
    notificationsManager: NotificationsManager
): StatusProvider = DebugStatusProviderImpl(notificationsManager)

fun createStatusProvider(
    networkManager: NetworkManager,
    notificationsManager: NotificationsManager
): StatusProvider = NetworkStatusProvider(networkManager, notificationsManager)
