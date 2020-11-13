package com.nevmem.qms.toast.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.TintableBackgroundView

class ToastView : LinearLayoutCompat, TintableBackgroundView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var backgroundTintData: BackgroundTintData = BackgroundTintData()

    fun wrapBackground(background: Drawable?, setter: (Drawable?) -> Unit) {
        val wrapper = background?.let { DrawableCompat.wrap(it) }
        backgroundTintData.wrapper = wrapper
        backgroundTintData.update()
        setter(wrapper ?: background)
    }

    override fun getSupportBackgroundTintList(): ColorStateList? {
        return backgroundTintData.tint
    }

    override fun setSupportBackgroundTintList(tintColors: ColorStateList?) {
        backgroundTintData.tint = tintColors
    }

    override fun getSupportBackgroundTintMode(): PorterDuff.Mode? {
        return backgroundTintData.mode
    }

    override fun setSupportBackgroundTintMode(tintMode: PorterDuff.Mode?) {
        backgroundTintData.mode = tintMode
    }
}

class BackgroundTintData {
    var tint: ColorStateList? = null
        set(value) {
            field = value
            update()
        }
    var mode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN
        set(value) {
            field = value
            update()
        }
    var wrapper: Drawable? = null

    fun update() {
        wrapper?.let { wrapper ->
            DrawableCompat.setTintList(wrapper, tint)
            mode?.let { DrawableCompat.setTintMode(wrapper, it) }
        }
    }
}

