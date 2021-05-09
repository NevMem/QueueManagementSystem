package com.nevmem.qms.push.repository

import com.nevmem.qms.push.data.RepoFeedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<RepoFeedback, Long> {
    fun findByEntityId(entityId: String): List<RepoFeedback>
}
