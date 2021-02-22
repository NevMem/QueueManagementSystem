package com.nevmem.qms

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.nevmem.qms.permissions.*
import com.nevmem.qms.toast.manager.ToastProvider
import com.nevmem.qms.toast.ui.ToastContainer
import com.nevmem.qms.usecase.BottomBarHidingUsecase
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

private const val ACTIVITY_PERMISSIONS_REQUEST_CODE = 123

class MainActivity : AppCompatActivity(), PermissionsDelegate {

    private lateinit var bottomBarHidingUsecase: BottomBarHidingUsecase

    private val toastProvider: ToastProvider by inject()
    private val permissionsManager: PermissionsManager by inject()

    private var currentPermissionsRequest: PermissionsRequest? = null
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (field == null) {
                checkPermissionsRequests()
            }
        }


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

    override fun onResume() {
        super.onResume()
        permissionsManager.registerDelegate(this)
        checkPermissionsRequests()
    }

    override fun onPause() {
        super.onPause()
        permissionsManager.removeDelegate(this)
    }

    override fun onHasNewPermissionsRequest() {
        checkPermissionsRequests()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_PERMISSIONS_REQUEST_CODE && currentPermissionsRequest != null) {
            val partitioned = currentPermissionsRequest!!.permissions.map { permission ->
                val index = permissions.indexOfFirst { it == permission.androidPermission }
                when {
                    grantResults[index] == PackageManager.PERMISSION_GRANTED -> {
                        PartitionedPermissionsResponse(permission, PermissionStatus.GRANTED)
                    }
                    shouldShowRequestPermissionRationale(permission.androidPermission) -> {
                        PartitionedPermissionsResponse(permission, PermissionStatus.DENIED)
                    }
                    else -> {
                        PartitionedPermissionsResponse(permission, PermissionStatus.DENIED_AND_CANNOT_RETRY)
                    }
                }
            }
            val response = PermissionsResponse(partitioned)
            currentPermissionsRequest!!.callback(response)
            currentPermissionsRequest = null
        }
    }

    override fun hasPermission(permission: Permission): Boolean =
        ContextCompat.checkSelfPermission(this, permission.androidPermission) == PackageManager.PERMISSION_GRANTED

    private fun checkPermissionsRequests() {
        if (!permissionsManager.hasRequests && currentPermissionsRequest == null) {
            return
        }

        val request = permissionsManager.popRequest()
        currentPermissionsRequest = request
        ActivityCompat.requestPermissions(
            this,
            request.permissions.map { it.androidPermission }.toTypedArray(),
            ACTIVITY_PERMISSIONS_REQUEST_CODE)
    }

    private fun BottomNavigationView.setupCorners() {
        val barBackground = background as MaterialShapeDrawable
        barBackground.shapeAppearanceModel = barBackground.shapeAppearanceModel.toBuilder()
            .setAllCornerSizes(resources.getDimensionPixelSize(R.dimen.indent).toFloat())
            .build()
    }
}
