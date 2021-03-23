package com.nevmem.qms.fragments.join.step.gallery

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.nevmem.qms.R
import com.nevmem.qms.inflate
import com.nevmem.qms.recycler.RVHolder
import com.nevmem.qms.recycler.RVItem
import com.nevmem.qms.recycler.RVItemFactory
import kotlinx.android.synthetic.main.layout_gallery_item.view.*

class GalleryItemFactory(private val context: Context) : RVItemFactory {
    inner class GalleryImage(view: View): RVHolder(view) {
        override fun onBind(item: RVItem) {
            item as GalleryItem
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(itemView.image)
        }
    }

    override fun isAppropriateType(item: RVItem): Boolean = item is GalleryItem
    override fun createHolder(root: ViewGroup): RVHolder
            = GalleryImage(context.inflate(R.layout.layout_gallery_item, root))
}
