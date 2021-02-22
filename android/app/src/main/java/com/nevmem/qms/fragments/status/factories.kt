package com.nevmem.qms.fragments.status

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.nevmem.qms.R
import com.nevmem.qms.features.FeatureManager
import com.nevmem.qms.features.isFeatureEnabled
import com.nevmem.qms.inflate
import com.nevmem.qms.knownfeatures.KnownFeatures
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.status_suggest_view.view.*
import kotlinx.android.synthetic.main.status_suggests_header.view.*

class HeaderFactory(private val context: Context) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as HeaderItem
            itemView.textView.text = context.resources.getText(R.string.status_suggests_header)
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is HeaderItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(R.layout.status_suggests_header, root))
}

class SuggestsFactory(
    private val context: Context,
    private val featureManager: FeatureManager
) : RVItemFactory {
    inner class Holder(view: View) : RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as SuggestItem
            itemView.suggestTitle.text = item.suggest.queue.name
            itemView.suggestDescription.text = item.suggest.queue.description
            item.suggest.queue.imageUrl?.let { url ->
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.icon_image_placeholder)
                    .into(itemView.suggestImage)
            }
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is SuggestItem
    override fun createHolder(root: ViewGroup): RVHolder
        = Holder(context.inflate(layoutResId(), root))

    private fun layoutResId(): Int
        = if (featureManager.isFeatureEnabled(KnownFeatures.UseCardsForSuggestsOnStatusPage.value))
            R.layout.status_suggest_view_with_card
        else
            R.layout.status_suggest_view
}
