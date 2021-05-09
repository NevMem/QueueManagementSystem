package com.nevmem.qms.push.service

import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.push.data.RepoFeedback
import com.nevmem.qms.push.repository.FeedbackRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FeedbacksService @Autowired constructor(
    private val repository: FeedbackRepository
) {
    fun loadFeedbacksForEntityId(entityId: String): List<Feedback> {
        return repository.findByEntityId(entityId).map {
            Feedback(it.entityId, it.authorEmail, it.text, it.rating)
        }
    }

    fun publishFeedback(feedback: Feedback) {
        check(feedback.score >= 0)
        val repoObj = RepoFeedback(feedback.author, feedback.entityId, feedback.text, feedback.score)
        repository.save(repoObj)
    }
}
