package com.nevmem.qms.feedback.recycler

import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.recycler.RVItem

object LoadingFeedbackItem : RVItem()
object NoFeedbackItem : RVItem()
data class ErrorFeedbackItem(val error: String) : RVItem()
data class FeedbackItem(val feedback: Feedback) : RVItem()
