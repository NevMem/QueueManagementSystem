package com.nevmem.qms.tags

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnAttach
import androidx.core.view.updatePadding

class TagView : AppCompatTextView {
    constructor(ctx: Context): super(ctx)
    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defaultStyle: Int): super(ctx, attrs, defaultStyle)

    init {
        val indent = context.resources.getDimensionPixelSize(R.dimen.indent)
        updatePadding(left = indent * 2, right = indent * 2, top = indent / 2, bottom = indent / 2)
        setBackgroundResource(R.drawable.tag_view_background)

        doOnAttach {
            val lmp = it.layoutParams as ViewGroup.MarginLayoutParams
            lmp.marginEnd = indent
            lmp.bottomMargin = indent
            it.layoutParams = lmp
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        backgroundTintList = ColorStateList.valueOf(TagsColorizer.colorFor(context, text.toString()))
    }
}
