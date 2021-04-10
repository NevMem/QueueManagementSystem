package com.nevmem.qms.ui.service

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView

class SmallServiceInfoView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    var state: SmallServiceViewViewModelDelegate.State = SmallServiceViewViewModelDelegate.State.None
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateUi()
        }

    private var ready: Boolean = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        ready = true
        updateUi()
    }

    private fun updateUi() {
        if (!ready) {
            return
        }
        removeAllViews()
        when (val curState = state) {
            SmallServiceViewViewModelDelegate.State.Loading -> {
                addView(ProgressBar(context), ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))
            }
            is SmallServiceViewViewModelDelegate.State.Data -> {
                val name = curState.service.info.name
                addView(AppCompatTextView(context).apply {
                        text = name
                    },
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT))
            }
        }
    }
}
