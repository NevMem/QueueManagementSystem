package com.nevmem.qms.feedback.recycler.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import com.nevmem.qms.feedback.R
import com.nevmem.qms.feedback.recycler.FeedbackItem
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory

class FeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as FeedbackItem
            itemView.findViewById<AppCompatTextView>(R.id.authorText).text = item.feedback.author
            itemView.findViewById<AppCompatTextView>(R.id.text).text = item.feedback.text
            itemView.findViewById<AppCompatRatingBar>(R.id.ratingBar).rating = item.feedback.score.toFloat()
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is FeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
            = Holder(context.inflate(R.layout.layout_feedback_item, root))
}
