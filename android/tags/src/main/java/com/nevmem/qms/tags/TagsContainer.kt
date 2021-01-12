package com.nevmem.qms.tags

import android.content.Context
import android.util.AttributeSet
import com.google.android.flexbox.FlexWrap.WRAP
import com.google.android.flexbox.FlexboxLayout

class TagsContainer : FlexboxLayout {
    constructor(ctx: Context): super(ctx)
    constructor(ctx: Context, attrs: AttributeSet): this(ctx, attrs, 0)
    constructor(ctx: Context, attrs: AttributeSet, defaultStyle: Int): super(ctx, attrs, defaultStyle) {
        ctx.theme.obtainStyledAttributes(attrs, R.styleable.TagsContainer, 0, 0).apply {
            try {
                tagsColorized = getBoolean(R.styleable.TagsContainer_colorized, false)
            } finally {
                recycle()
            }
        }
    }

    var tagsColorized = false

    init {
        flexWrap = WRAP
    }

    fun setTags(tags: List<String>) {
        removeAllViews()
        tags.map { TagView(context).apply { text = it; colorized = tagsColorized } }.forEach { tagView ->
            addView(tagView)
        }
    }
}
