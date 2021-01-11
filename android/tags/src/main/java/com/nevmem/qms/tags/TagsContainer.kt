package com.nevmem.qms.tags

import android.content.Context
import android.util.AttributeSet
import com.google.android.flexbox.FlexWrap.WRAP
import com.google.android.flexbox.FlexboxLayout

class TagsContainer : FlexboxLayout {
    constructor(ctx: Context): super(ctx)
    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defaultStyle: Int): super(ctx, attrs, defaultStyle)

    init {
        flexWrap = WRAP
    }

    fun setTags(tags: List<String>) {
        removeAllViews()
        tags.map { TagView(context).apply { text = it } }.forEach { tagView ->
            addView(tagView)
        }
    }
}
