package com.nevmem.qms.feedback.internal

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.data.feedback.PublishFeedbackRequest
import com.nevmem.qms.feedback.FeedbackManager
import com.nevmem.qms.network.NetworkManager

internal class FeedbackManagerImpl(
    private val authManager: AuthManager,
    private val networkManager: NetworkManager
) : FeedbackManager {
    override suspend fun loadFeedback(entityId: String): List<Feedback>
        = networkManager.loadFeedback(entityId, authManager.token)

    override suspend fun publishFeedback(request: PublishFeedbackRequest)
        = networkManager.publishFeedback(request, authManager.token)
}
