package com.nevmem.qms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.nevmem.qms.usecase.BottomBarHidingUsecase
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBarHidingUsecase: BottomBarHidingUsecase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = resources.getColor(R.color.backgroundColor, theme)

        val navController = findNavController(R.id.navigationHost)
        navView.setupWithNavController(navController)

        bottomBarHidingUsecase = BottomBarHidingUsecase(this, navView, listOf(
            R.id.navigation_scan,
            R.id.navigation_home))

        navController.addOnDestinationChangedListener { _, destination, _ ->
            YandexMetrica.reportEvent("navigation.destination-change", mapOf(
                "destination-label" to destination.label
            ))
        }
    }
}
