package com.nevmem.qms.toast.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.nevmem.qms.toast.R
import com.nevmem.qms.toast.manager.ToastData
import com.nevmem.qms.toast.manager.ToastProvider
import kotlinx.android.synthetic.main.layout_toast.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ToastContainer : LinearLayoutCompat, ToastProvider.Listener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var toastProvider: ToastProvider

    private var toastShowing = false

    fun setToastProvider(provider: ToastProvider) {
        check(!::toastProvider.isInitialized) { "Cannot reinitialize toast provider" }
        toastProvider = provider

        toastProvider.addListener(this)
        onHasToastsChanged()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        toastProvider.removeListener(this)
    }

    override fun onHasToastsChanged() {
        showToastIfNeeded()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        orientation = VERTICAL
    }

    private fun showToastIfNeeded() {
        if (toastShowing || !toastProvider.hasToast) {
            return
        }
        toastShowing = true
        val toast = toastProvider.consumeOneToast()
        GlobalScope.launch(Dispatchers.Main) {
            val view = showToast(toast)
            delay(3000)
            removeToast(view)
            toastShowing = false
            showToastIfNeeded()
        }
    }

    private fun showToast(data: ToastData): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_toast, this, true).apply {
            setBackgroundResource(R.drawable.toast_error)
            toastMessage.text = data.message

            alpha = 0f
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 100

                addUpdateListener {
                    alpha = it.animatedValue as Float
                }

                start()
            }
        }
    }

    private suspend fun removeToast(view: View) = suspendCoroutine<Unit> { continuation ->
        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 100

            addUpdateListener {
                view.alpha = it.animatedValue as Float
            }

            addOnEndListener {
                removeAllViews()
                continuation.resume(Unit)
            }

            start()
        }
    }

    private fun ValueAnimator.addOnEndListener(listener: () -> Unit) {
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationStart(p0: Animator?) = Unit

            override fun onAnimationEnd(p0: Animator?) {
                listener()
            }
        })
    }
}
