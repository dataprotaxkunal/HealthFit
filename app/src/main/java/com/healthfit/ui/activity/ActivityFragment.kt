package com.healthfit.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthfit.databinding.FragmentActivityBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class ActivityFragment : Fragment() {

    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthViewModel
    private lateinit var adapter: ActivityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            HealthViewModelFactory(requireActivity().application)
        )[HealthViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = ActivityAdapter { activity -> viewModel.deleteActivity(activity) }
        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddActivity.setOnClickListener {
            startActivity(Intent(requireContext(), AddActivityActivity::class.java))
        }
    }

    private fun observeData() {
        // Today's step ring
        viewModel.todaySteps.observe(viewLifecycleOwner) { steps ->
            val count = steps?.steps ?: 0
            val goal = steps?.goal ?: 10000
            binding.tvStepsToday.text = "$count / $goal steps"
            binding.progressStepsRing.progress = ((count.toFloat() / goal) * 100).toInt().coerceAtMost(100)
            binding.tvCaloriesToday.text = "${(steps?.caloriesBurned ?: 0f).toInt()} kcal burned"
            binding.tvDistanceToday.text = String.format("%.2f km", steps?.distanceKm ?: 0f)
        }

        // Recent activities list
        viewModel.allActivities.observe(viewLifecycleOwner) { activities ->
            adapter.submitList(activities)
            binding.tvEmptyState.visibility = if (activities.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
