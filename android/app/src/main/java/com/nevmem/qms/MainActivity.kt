package com.nevmem.qms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.nevmem.qms.toast.manager.ToastProvider
import com.nevmem.qms.toast.ui.ToastContainer
import com.nevmem.qms.usecase.BottomBarHidingUsecase
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBarHidingUsecase: BottomBarHidingUsecase

    private val toastProvider: ToastProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = resources.getColor(R.color.backgroundColor, theme)

        val navController = findNavController(R.id.navigationHost)
        navView.setupWithNavController(navController)
        navView.setupCorners()

        bottomBarHidingUsecase = BottomBarHidingUsecase(this, navView, listOf(
            R.id.navigation_join,
            R.id.navigation_profile,
            R.id.navigation_status))

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomBarHidingUsecase.onDestinationChanged(destination)
            YandexMetrica.reportEvent("navigation.destination-change", mapOf(
                "destination-label" to destination.label
            ))
        }

        findViewById<ToastContainer>(R.id.toastContainer).setToastProvider(toastProvider)
    }

    private fun BottomNavigationView.setupCorners() {
        val barBackground = background as MaterialShapeDrawable
        barBackground.shapeAppearanceModel = barBackground.shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(resources.getDimensionPixelSize(R.dimen.indent).toFloat())
            .build()
    }
}
