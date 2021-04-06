package com.nevmem.qms.feedback.recycler.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.nevmem.qms.feedback.R
import com.nevmem.qms.feedback.recycler.NoFeedbackItem
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory

class NoFeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) = Unit
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is NoFeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
            = Holder(context.inflate(R.layout.layout_no_feedback_item, root))
}
