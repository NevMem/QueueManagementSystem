package com.nevmem.qms.data.feedback

data class LoadFeedbacksRequest(
    var entityId: String = "")
data class PublishFeedbackRequest(
    var entityId: String = "", var text: String = "", var score: Double = -1.0)
data class Feedback(
    var entityId: String = "",
    var author: String = "",
    var text: String = "",
    var score: Double = -1.0)
data class LoadRatingRequest(
    var entityId: String)
data class LoadRatingResponse(
    var rating: Float)
