package com.nevmem.qms.feedback.repository

import com.nevmem.qms.feedback.data.RepoFeedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<RepoFeedback, Long> {
    fun findByEntityId(entityId: String): List<RepoFeedback>
}
