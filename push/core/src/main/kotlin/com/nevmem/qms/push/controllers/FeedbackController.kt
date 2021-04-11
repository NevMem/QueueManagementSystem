package com.nevmem.qms.push.controllers

import com.google.api.client.util.Base64
import com.google.api.client.util.StringUtils
import com.google.gson.Gson
import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.data.feedback.LoadFeedbacksRequest
import com.nevmem.qms.data.feedback.PublishFeedbackRequest
import com.nevmem.qms.push.client.BackendClient
import com.nevmem.qms.push.service.FeedbacksService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import javax.xml.ws.Response

@RestController
@CrossOrigin("*")
@RequestMapping("api/feedback")
class FeedbackController @Autowired constructor(
    private val service: FeedbacksService,
    private val client: BackendClient
) {

    data class SessionData(var user: String = "")

    @PostMapping("/load")
    fun load(
        @RequestHeader("session") session: String,
        @RequestBody body: LoadFeedbacksRequest
    ): ResponseEntity<List<Feedback>> = client.withCheckAuth(session) {
        service.loadFeedbacksForEntityId(body.entityId)
    }

    @PostMapping("/publish")
    fun publishFeedback(
        @RequestHeader("session") session: String,
        @RequestBody body: PublishFeedbackRequest
    ) {
        client.checkAuth(session)
        val data = parseSession(session)
        val feedback = Feedback(body.entityId, data.user, body.text, body.score)
        service.publishFeedback(feedback)
    }

    private fun parseSession(session: String): SessionData {
        val data = StringUtils.newStringUtf8(Base64.decodeBase64(session.split(".").first()))
        return Gson().fromJson(data, SessionData::class.java)
    }

}
