package com.nevmem.qms.usecase

import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomBarHidingUsecase(
    private val lifecycleOwner: LifecycleOwner,
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

    private fun showNavBar() {
        bottomNavigationView.isVisible = true
    }

    private fun hideNavBar() {
        bottomNavigationView.isVisible = false
    }
}
