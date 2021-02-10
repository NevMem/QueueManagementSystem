package com.nevmem.qms.usecase

import android.animation.ValueAnimator
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val bottomBarTransitionDelay = 300L

class BottomBarHidingUsecase(
    lifecycleOwner: LifecycleOwner,
    private val bottomNavigationView: BottomNavigationView,
    private val listVisibleDestinations: List<Int>
): LifecycleObserver {

    private var resumed = true

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun onDestinationChanged(destination: NavDestination) {
        if (!resumed) return

        if (destination.id in listVisibleDestinations) {
            showNavBar()
        } else {
            hideNavBar()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        resumed = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        resumed = false
    }

    private var animator: ValueAnimator? = null
    private var currentTranslationY: Float = 0f

    private fun showNavBar() {
        bottomNavigationView.isVisible = true
        animator?.cancel()
        currentTranslationY = bottomNavigationView.translationY
        animator = ValueAnimator.ofFloat(currentTranslationY, 0f).apply {
            duration = bottomBarTransitionDelay
            addUpdateListener {
                currentTranslationY = (it.animatedValue as Float)
                bottomNavigationView.translationY = currentTranslationY
            }
        }
        animator?.start()
    }

    private fun hideNavBar() {
        animator?.cancel()
        currentTranslationY = bottomNavigationView.translationY
        animator = ValueAnimator.ofFloat(currentTranslationY, bottomNavigationView.height + 100f).apply {
            duration = bottomBarTransitionDelay
            addUpdateListener {
                currentTranslationY = (it.animatedValue as Float)
                bottomNavigationView.translationY = currentTranslationY
            }
        }
        animator?.start()
    }
}
