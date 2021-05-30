package com.nevmem.qms.push

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.logger.Logger
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.push.internal.PushManagerImpl

fun createPushManager(
    lifecycleOwner: LifecycleOwner,
    networkManager: NetworkManager,
    authManager: AuthManager,
    context: Context,
    logger: Logger
): PushManager = PushManagerImpl(lifecycleOwner, networkManager, authManager, context, logger)
