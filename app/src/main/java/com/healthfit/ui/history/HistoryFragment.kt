package com.healthfit.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.healthfit.databinding.FragmentHistoryBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            HealthViewModelFactory(requireActivity().application)
        )[HealthViewModel::class.java]

        observeData()
    }

    private fun observeData() {
        // Summary counts
        viewModel.allGlucoseReadings.observe(viewLifecycleOwner) { readings ->
            binding.tvGlucoseCount.text = "Total Glucose Readings: ${readings.size}"
            if (readings.isNotEmpty()) {
                val avg = readings.map { it.glucoseLevel }.average()
                binding.tvGlucoseAvg.text = String.format("Average: %.1f mg/dL", avg)
                val high = readings.count { it.glucoseLevel > 140 }
                val normal = readings.count { it.glucoseLevel in 70f..140f }
                val low = readings.count { it.glucoseLevel < 70 }
                binding.tvGlucoseBreakdown.text = "Normal: $normal  High: $high  Low: $low"
            }
        }

        viewModel.allBPReadings.observe(viewLifecycleOwner) { readings ->
            binding.tvBpCount.text = "Total BP Readings: ${readings.size}"
            if (readings.isNotEmpty()) {
                val avgSys = readings.map { it.systolic }.average()
                val avgDia = readings.map { it.diastolic }.average()
                binding.tvBpAvg.text = String.format("Average: %.0f/%.0f mmHg", avgSys, avgDia)
            }
        }

        viewModel.allActivities.observe(viewLifecycleOwner) { activities ->
            binding.tvActivityCount.text = "Total Activities Logged: ${activities.size}"
            if (activities.isNotEmpty()) {
                val totalCal = activities.sumOf { it.caloriesBurned.toDouble() }
                val totalMin = activities.sumOf { it.durationMinutes }
                binding.tvActivitySummary.text = String.format(
                    "Total: %.0f kcal burned  •  %d minutes", totalCal, totalMin
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
