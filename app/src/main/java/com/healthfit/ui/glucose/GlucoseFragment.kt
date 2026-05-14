package com.healthfit.ui.glucose

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthfit.databinding.FragmentGlucoseBinding
import com.healthfit.viewmodel.HealthViewModel
import com.healthfit.viewmodel.HealthViewModelFactory

class GlucoseFragment : Fragment() {

    private var _binding: FragmentGlucoseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthViewModel
    private lateinit var adapter: GlucoseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGlucoseBinding.inflate(inflater, container, false)
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
        adapter = GlucoseAdapter { reading ->
            viewModel.deleteGlucose(reading)
        }
        binding.rvGlucoseReadings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGlucoseReadings.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddGlucose.setOnClickListener {
            startActivity(Intent(requireContext(), AddGlucoseActivity::class.java))
        }
    }

    private fun observeData() {
        viewModel.allGlucoseReadings.observe(viewLifecycleOwner) { readings ->
            adapter.submitList(readings)
            binding.tvEmptyState.visibility = if (readings.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
