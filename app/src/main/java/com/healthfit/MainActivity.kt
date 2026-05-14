package com.healthfit

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.healthfit.databinding.ActivityMainBinding
import com.healthfit.service.StepCounterService
import com.healthfit.util.PermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            StepCounterService.startService(this)
        } else {
            Snackbar.make(binding.root,
                "Step counting and GPS require permissions. Grant them in Settings.",
                Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfig = AppBarConfiguration(setOf(
            R.id.navigation_dashboard, R.id.navigation_glucose,
            R.id.navigation_blood_pressure, R.id.navigation_activity, R.id.navigation_history
        ))
        setupActionBarWithNavController(navController, appBarConfig)
        navView.setupWithNavController(navController)

        if (PermissionHelper.hasAllPermissions(this)) {
            StepCounterService.startService(this)
        } else {
            permissionLauncher.launch(PermissionHelper.requiredPermissions())
        }
    }
}
