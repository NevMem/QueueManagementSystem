package com.nevmem.qms.feedback.controllers

import com.nevmem.qms.data.feedback.LoadRatingRequest
import com.nevmem.qms.data.feedback.LoadRatingResponse
import com.nevmem.qms.feedback.client.BackendClient
import com.nevmem.qms.feedback.service.FeedbacksService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
class RatingsController @Autowired constructor(
    private val feedbacksService: FeedbacksService,
    private val client: BackendClient
) {

    @PostMapping("/rating")
    fun getRating(
        @RequestHeader("session") session: String,
        @RequestBody body: LoadRatingRequest
    ): LoadRatingResponse {
        client.checkAuth(session)
        val averageValue = feedbacksService.loadFeedbacksForEntityId(body.entityId).map { it.score }
            .average()
        if (averageValue.isNaN()) {
            return LoadRatingResponse(null)
        }
        return LoadRatingResponse(averageValue.toFloat())
    }
}
