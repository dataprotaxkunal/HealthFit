package com.healthfit.ui.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.healthfit.data.entities.ActivityType
import com.healthfit.databinding.ActivityAddActivityBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class AddActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddActivityBinding
    private lateinit var viewModel: HealthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Log Activity"
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProvider(
            this,
            HealthViewModelFactory(application)
        )[HealthViewModel::class.java]

        setupSpinner()
        setupSaveButton()
    }

    private fun setupSpinner() {
        val types = ActivityType.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerActivityType.adapter = adapter
    }

    private fun setupSaveButton() {
        binding.btnSaveActivity.setOnClickListener {
            val durationStr = binding.etDuration.text.toString().trim()
            if (durationStr.isEmpty()) { binding.etDuration.error = "Required"; return@setOnClickListener }

            val duration = durationStr.toIntOrNull()
            if (duration == null || duration <= 0) {
                binding.etDuration.error = "Enter valid duration in minutes"; return@setOnClickListener
            }

            val activityType = ActivityType.values()[binding.spinnerActivityType.selectedItemPosition]
            val calories = binding.etCalories.text.toString().trim().toFloatOrNull() ?: 0f
            val distance = binding.etDistance.text.toString().trim().toFloatOrNull() ?: 0f
            val steps = binding.etSteps.text.toString().trim().toIntOrNull() ?: 0
            val notes = binding.etNotes.text.toString().trim()

            viewModel.insertActivity(activityType, duration, calories, distance, steps, notes)
            Toast.makeText(this, "Activity logged!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
