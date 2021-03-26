package com.nevmem.qms.feedback

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.feedback.internal.FeedbackManagerImpl
import com.nevmem.qms.network.NetworkManager

fun createFeedbackManager(authManager: AuthManager, networkManager: NetworkManager): FeedbackManager
    = FeedbackManagerImpl(authManager, networkManager)
