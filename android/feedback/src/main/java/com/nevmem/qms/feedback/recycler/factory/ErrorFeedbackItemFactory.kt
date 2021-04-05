package com.nevmem.qms.feedback.recycler.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.nevmem.qms.feedback.R
import com.nevmem.qms.feedback.recycler.ErrorFeedbackItem
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory

class ErrorFeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ErrorFeedbackItem
            itemView.findViewById<AppCompatTextView>(R.id.errorText).text = item.error
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ErrorFeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
            = Holder(context.inflate(R.layout.layout_error_feedback_item, root))
}
