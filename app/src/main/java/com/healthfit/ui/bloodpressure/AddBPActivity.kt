package com.healthfit.ui.bloodpressure

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.healthfit.databinding.ActivityAddBpBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class AddBPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBpBinding
    private lateinit var viewModel: HealthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Log Blood Pressure"
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProvider(
            this,
            HealthViewModelFactory(application)
        )[HealthViewModel::class.java]

        setupSaveButton()
        showBPGuide()
    }

    private fun showBPGuide() {
        binding.tvGuide.text = """
            📊 Blood Pressure Reference:
            • Normal: < 120/80 mmHg
            • Elevated: 120–129 / < 80 mmHg
            • High Stage 1: 130–139 / 80–89 mmHg
            • High Stage 2: ≥ 140 / ≥ 90 mmHg
            • Crisis: > 180 / > 120 mmHg
        """.trimIndent()
    }

    private fun setupSaveButton() {
        binding.btnSaveBP.setOnClickListener {
            val systolicStr = binding.etSystolic.text.toString().trim()
            val diastolicStr = binding.etDiastolic.text.toString().trim()

            if (systolicStr.isEmpty()) { binding.etSystolic.error = "Required"; return@setOnClickListener }
            if (diastolicStr.isEmpty()) { binding.etDiastolic.error = "Required"; return@setOnClickListener }

            val systolic = systolicStr.toIntOrNull()
            val diastolic = diastolicStr.toIntOrNull()
            val pulse = binding.etPulse.text.toString().trim().toIntOrNull() ?: 0

            if (systolic == null || systolic !in 60..300) {
                binding.etSystolic.error = "Enter valid systolic (60–300)"; return@setOnClickListener
            }
            if (diastolic == null || diastolic !in 40..200) {
                binding.etDiastolic.error = "Enter valid diastolic (40–200)"; return@setOnClickListener
            }

            val notes = binding.etNotes.text.toString().trim()
            viewModel.insertBP(systolic, diastolic, pulse, notes)
            Toast.makeText(this, "Blood pressure saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
