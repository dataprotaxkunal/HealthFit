package com.healthfit.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.healthfit.service.StepCounterService
import com.healthfit.util.PermissionHelper

/**
 * Starts the StepCounterService automatically after the phone reboots.
 * Without this, the service dies on reboot and steps are not counted until
 * the user opens the app again.
 *
 * Requires RECEIVE_BOOT_COMPLETED permission in the manifest.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        Log.d("HealthFit", "Boot received — restarting StepCounterService")
        if (PermissionHelper.hasStepPermission(context)) {
            StepCounterService.startService(context)
        }
    }
}
