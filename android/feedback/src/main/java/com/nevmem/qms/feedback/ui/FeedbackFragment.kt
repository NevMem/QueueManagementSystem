package com.nevmem.qms.feedback.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRatingBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nevmem.qms.common.utils.runOnUi
import com.nevmem.qms.data.feedback.PublishFeedbackRequest
import com.nevmem.qms.feedback.FeedbackManager
import com.nevmem.qms.feedback.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedbackFragment : BottomSheetDialogFragment() {

    internal var feedbackManager: FeedbackManager? = null
    internal var entityId: String? = null

    private val feedbackText: TextInputEditText
        get() = view!!.findViewById(R.id.feedbackText)

    private val ratingBar: AppCompatRatingBar
        get() = view!!.findViewById(R.id.ratingBar)

    private val button: MaterialButton
        get() = view!!.findViewById(R.id.leaveFeedbackButton)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_feedback_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.leaveFeedbackButton).setOnClickListener {
            publish()
        }
    }

    private fun publish() {
        val manager = feedbackManager
        val id = entityId
        val rating = ratingBar.rating

        val views = listOf(feedbackText, ratingBar, button)

        if (manager != null && id != null && rating > 0) {
            views.forEach { it.isEnabled = false }
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    manager.publishFeedback(PublishFeedbackRequest(id, feedbackText.text.toString(), rating.toDouble()))
                } catch (exception: Exception) {}
                runOnUi {
                    views.forEach { it.isEnabled = true }
                    dismiss()
                }
            }
        } else {
            dismiss()
        }
    }

    companion object {
        fun newInstance(feedbackManager: FeedbackManager, entityId: String) = FeedbackFragment().apply {
            this.feedbackManager = feedbackManager
            this.entityId = entityId
        }
    }
}
