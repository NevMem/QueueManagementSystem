package com.nevmem.qms.tags

import android.content.Context

internal object TagsColorizer {
    private val resIds = listOf(
        R.color.tag_background_tint_color_1,
        R.color.tag_background_tint_color_2,
        R.color.tag_background_tint_color_3,
        R.color.tag_background_tint_color_4,
        R.color.tag_background_tint_color_5,
        R.color.tag_background_tint_color_6,
        R.color.tag_background_tint_color_7
    )

    private var index = 0

    private var alreadyAssigned = mutableMapOf<String, Int>()

    fun colorFor(ctx: Context, text: String): Int {
        if (text in alreadyAssigned) {
            return alreadyAssigned[text]!!.colorFromResources(ctx)
        }
        alreadyAssigned[text] = resIds[index]
        index += 1
        index %= resIds.size
        return alreadyAssigned[text]!!.colorFromResources(ctx)
    }

    private fun Int.colorFromResources(ctx: Context): Int
        = ctx.resources.getColor(this, ctx.theme)
}
