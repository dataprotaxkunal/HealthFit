package com.healthfit.ui.bloodpressure

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthfit.databinding.FragmentBloodPressureBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class BloodPressureFragment : Fragment() {

    private var _binding: FragmentBloodPressureBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthViewModel
    private lateinit var adapter: BPAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBloodPressureBinding.inflate(inflater, container, false)
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
        adapter = BPAdapter { reading -> viewModel.deleteBP(reading) }
        binding.rvBPReadings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBPReadings.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddBP.setOnClickListener {
            startActivity(Intent(requireContext(), AddBPActivity::class.java))
        }
    }

    private fun observeData() {
        viewModel.allBPReadings.observe(viewLifecycleOwner) { readings ->
            adapter.submitList(readings)
            binding.tvEmptyState.visibility = if (readings.isEmpty()) View.VISIBLE else View.GONE

            // Show latest reading summary
            readings.firstOrNull()?.let { latest ->
                binding.tvLatestBP.text = latest.getFormattedReading()
                binding.tvLatestCategory.text = latest.getCategory().name.replace("_", " ")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
