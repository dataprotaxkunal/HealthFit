package com.healthfit.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionHelper {

    // All permissions the app needs
    fun requiredPermissions(): Array<String> {
        val base = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        // ACTIVITY_RECOGNITION needed on Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            base.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        // FOREGROUND_SERVICE_LOCATION needed on Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            base.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }
        return base.toTypedArray()
    }

    fun hasStepPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAllPermissions(context: Context): Boolean =
        hasStepPermission(context) && hasLocationPermission(context)

    /**
     * Register a permission launcher from a Fragment.
     * Call this in Fragment.onCreate() before the view is created.
     *
     * Usage:
     *   val launcher = PermissionHelper.registerLauncher(this) { granted -> ... }
     *   launcher.launch(PermissionHelper.requiredPermissions())
     */
    fun registerLauncher(
        fragment: Fragment,
        onResult: (allGranted: Boolean) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            onResult(results.values.all { it })
        }
    }

    fun registerLauncher(
        activity: Activity,
        launcher: ActivityResultLauncher<Array<String>>,
        onResult: (allGranted: Boolean) -> Unit
    ) { /* use from Activity — call launcher.launch() yourself */ }
}
