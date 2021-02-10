package com.nevmem.qms.status

import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.notifications.NotificationsManager
import com.nevmem.qms.status.internal.StatusProviderImpl


fun createStatusProvider(
    networkManager: NetworkManager,
    notificationsManager: NotificationsManager
): StatusProvider = StatusProviderImpl(networkManager, notificationsManager)
