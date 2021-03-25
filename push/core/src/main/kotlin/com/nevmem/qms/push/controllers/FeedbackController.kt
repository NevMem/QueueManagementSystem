package com.nevmem.qms.push.controllers

import com.google.api.client.util.Base64
import com.google.api.client.util.StringUtils
import com.google.gson.Gson
import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.data.feedback.LoadFeedbacksRequest
import com.nevmem.qms.data.feedback.PublishFeedbackRequest
import com.nevmem.qms.push.service.FeedbacksService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping("api/feedback")
class FeedbackController @Autowired constructor(
    private val service: FeedbacksService
) {

    data class SessionData(var user: String = "")

    @PostMapping("/load")
    fun load(
        @RequestHeader("session") session: String,
        @RequestBody body: LoadFeedbacksRequest
    ): List<Feedback> {
        val data = parseSession(session)
        return service.loadFeedbacksForEntityId(body.entityId)
    }

    @PostMapping("/publish")
    fun publishFeedback(
        @RequestHeader("session") session: String,
        @RequestBody body: PublishFeedbackRequest
    ) {
        val data = parseSession(session)
        val feedback = Feedback(body.entityId, data.user, body.text, body.score)
        service.publishFeedback(feedback)
    }

    private fun parseSession(session: String): SessionData {
        val data = StringUtils.newStringUtf8(Base64.decodeBase64(session.split(".").first()))
        return Gson().fromJson(data, SessionData::class.java)
    }

}
