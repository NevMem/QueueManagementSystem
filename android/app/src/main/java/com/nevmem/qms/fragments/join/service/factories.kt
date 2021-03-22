package com.nevmem.qms.fragments.join.service

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.nevmem.qms.R
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
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
