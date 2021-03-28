package com.nevmem.qms.fragments.join.service

import com.nevmem.qms.data.feedback.Feedback
import com.nevmem.qms.recycler.RVItem

class DescriptionItem(val description: String) : RVItem()
object ChecklistHeader : RVItem()
class ChecklistItem(val check: String) : RVItem()

object LoadingFeedbackItem : RVItem()
object NoFeedbackItem : RVItem()
data class ErrorFeedbackItem(val error: String) : RVItem()
data class FeedbackItem(val feedback: Feedback) : RVItem()
