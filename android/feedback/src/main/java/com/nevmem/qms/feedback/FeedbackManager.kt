package com.nevmem.qms.feedback

import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.data.feedback.PublishFeedbackRequest

interface FeedbackManager {
    suspend fun loadFeedback(entityId: String): List<Feedback>

    suspend fun publishFeedback(request: PublishFeedbackRequest)

    fun createFeedbackAdapter(entityId: String): FeedbackAdapter
}
