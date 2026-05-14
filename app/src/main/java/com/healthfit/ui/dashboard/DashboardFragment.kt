package com.healthfit.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.healthfit.data.entities.BPCategory
import com.healthfit.data.entities.GlucoseStatus
import com.healthfit.databinding.FragmentDashboardBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
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
        // Latest Glucose
        viewModel.latestGlucose.observe(viewLifecycleOwner) { glucose ->
            if (glucose != null) {
                binding.tvGlucoseValue.text = "${glucose.glucoseLevel.toInt()} mg/dL"
                binding.tvGlucoseMealType.text = glucose.mealType.name.replace("_", " ")
                binding.tvGlucoseTime.text = glucose.time
                val status = glucose.getStatus()
                binding.tvGlucoseStatus.text = when (status) {
                    GlucoseStatus.NORMAL -> "✅ Normal"
                    GlucoseStatus.LOW -> "⬇️ Low"
                    GlucoseStatus.PRE_DIABETIC -> "⚠️ Pre-Diabetic"
                    GlucoseStatus.HIGH -> "🔴 High"
                }
            } else {
                binding.tvGlucoseValue.text = "-- mg/dL"
                binding.tvGlucoseStatus.text = "No data"
            }
        }

        // Latest BP
        viewModel.latestBP.observe(viewLifecycleOwner) { bp ->
            if (bp != null) {
                binding.tvBPValue.text = bp.getFormattedReading()
                binding.tvBPTime.text = bp.time
                val cat = bp.getCategory()
                binding.tvBPStatus.text = when (cat) {
                    BPCategory.NORMAL -> "✅ Normal"
                    BPCategory.LOW -> "⬇️ Low"
                    BPCategory.ELEVATED -> "⚠️ Elevated"
                    BPCategory.HIGH_STAGE1 -> "🔴 High Stage 1"
                    BPCategory.HIGH_STAGE2 -> "🔴 High Stage 2"
                    BPCategory.CRISIS -> "🚨 Crisis"
                }
                if (bp.pulse > 0) binding.tvBPPulse.text = "Pulse: ${bp.pulse} bpm"
            } else {
                binding.tvBPValue.text = "--/-- mmHg"
                binding.tvBPStatus.text = "No data"
            }
        }

        // Today's Steps
        viewModel.todaySteps.observe(viewLifecycleOwner) { steps ->
            val count = steps?.steps ?: 0
            val goal = steps?.goal ?: 10000
            val progress = ((count.toFloat() / goal) * 100).toInt().coerceAtMost(100)
            binding.tvStepsCount.text = "$count"
            binding.tvStepsGoal.text = "Goal: $goal"
            binding.progressSteps.progress = progress
            binding.tvCalories.text = "${(steps?.caloriesBurned ?: 0f).toInt()} kcal"
            binding.tvDistance.text = String.format("%.1f km", steps?.distanceKm ?: 0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
