package com.nevmem.qms.fragments.join.service

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.nevmem.qms.R
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.layout_checklist_item.view.*
import kotlinx.android.synthetic.main.layout_error_feedback_item.view.*
import kotlinx.android.synthetic.main.layout_feedback_item.view.*
import kotlinx.android.synthetic.main.layout_service_description.view.*

class DescriptionItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as DescriptionItem
            itemView.descriptionText.text = item.description
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is DescriptionItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_service_description, root))
}

class ChecklistHeaderFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) = Unit
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ChecklistHeader
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_checklist_header, root))
}

class ChecklistItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ChecklistItem
            itemView.checklistItem.text = item.check
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ChecklistItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_checklist_item, root))
}

internal class LoadingFeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) = Unit
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is LoadingFeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_loading_feedback_item, root))
}

internal class ErrorFeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as ErrorFeedbackItem
            itemView.errorText.text = item.error
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is ErrorFeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_error_feedback_item, root))
}

internal class FeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as FeedbackItem
            itemView.authorText.text = item.feedback.author
            itemView.text.text = item.feedback.text
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is FeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_feedback_item, root))
}

internal class NoFeedbackItemFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) = Unit
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is NoFeedbackItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.layout_no_feedback_item, root))
}
