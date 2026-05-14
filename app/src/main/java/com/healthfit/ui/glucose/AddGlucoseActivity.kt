package com.healthfit.ui.glucose

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.healthfit.data.entities.MealType
import com.healthfit.databinding.ActivityAddGlucoseBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class AddGlucoseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGlucoseBinding
    private lateinit var viewModel: HealthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGlucoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Log Blood Glucose"
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProvider(
            this,
            HealthViewModelFactory(application)
        )[HealthViewModel::class.java]

        setupMealTypeSpinner()
        setupSaveButton()
    }

    private fun setupMealTypeSpinner() {
        val mealTypes = MealType.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMealType.adapter = adapter
    }

    private fun setupSaveButton() {
        binding.btnSaveGlucose.setOnClickListener {
            val levelStr = binding.etGlucoseLevel.text.toString().trim()
            if (levelStr.isEmpty()) {
                binding.etGlucoseLevel.error = "Please enter glucose level"
                return@setOnClickListener
            }

            val level = levelStr.toFloatOrNull()
            if (level == null || level <= 0 || level > 999) {
                binding.etGlucoseLevel.error = "Enter a valid glucose level (1–999 mg/dL)"
                return@setOnClickListener
            }

            val mealType = MealType.values()[binding.spinnerMealType.selectedItemPosition]
            val notes = binding.etNotes.text.toString().trim()

            viewModel.insertGlucose(level, mealType, notes)
            Toast.makeText(this, "Glucose reading saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
