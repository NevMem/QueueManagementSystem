package com.nevmem.qms.push.service

import com.nevmem.qms.data.feedback.Feedback
import org.springframework.stereotype.Service

@Service
class FeedbacksService {

    private val feedbacks: MutableList<Feedback> = mutableListOf()

    fun loadFeedbacksForEntityId(entityId: String): List<Feedback> {
        return feedbacks.filter { it.entityId == entityId }
    }

    fun publishFeedback(feedback: Feedback) {
        feedbacks.add(feedback)
    }
}
