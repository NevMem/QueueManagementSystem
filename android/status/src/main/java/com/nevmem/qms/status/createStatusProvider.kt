package com.nevmem.qms.status

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.notifications.NotificationsManager
import com.nevmem.qms.status.internal.NetworkStatusProvider

fun createStatusProvider(
    networkManager: NetworkManager,
    notificationsManager: NotificationsManager,
    authManager: AuthManager
): StatusProvider = NetworkStatusProvider(networkManager, notificationsManager, authManager)
