package com.healthfit.ui.bloodpressure

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.healthfit.R
import com.healthfit.data.entities.BPCategory
import com.healthfit.data.entities.BloodPressure
import com.healthfit.databinding.ItemBloodPressureBinding

class BPAdapter(
    private val onDelete: (BloodPressure) -> Unit
) : ListAdapter<BloodPressure, BPAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBloodPressureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemBloodPressureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BloodPressure) {
            binding.tvBPReading.text = item.getFormattedReading()
            binding.tvSystolic.text = "Systolic: ${item.systolic}"
            binding.tvDiastolic.text = "Diastolic: ${item.diastolic}"
            binding.tvDateTime.text = "${item.date}  ${item.time}"
            if (item.pulse > 0) binding.tvPulse.text = "Pulse: ${item.pulse} bpm"
            if (item.notes.isNotBlank()) binding.tvNotes.text = item.notes

            val cat = item.getCategory()
            val (label, colorRes) = when (cat) {
                BPCategory.LOW         -> "Low" to R.color.status_low
                BPCategory.NORMAL      -> "Normal" to R.color.status_normal
                BPCategory.ELEVATED    -> "Elevated" to R.color.status_warning
                BPCategory.HIGH_STAGE1 -> "High Stage 1" to R.color.status_high
                BPCategory.HIGH_STAGE2 -> "High Stage 2" to R.color.status_high
                BPCategory.CRISIS      -> "⚠️ CRISIS" to R.color.status_crisis
            }
            binding.tvCategory.text = label
            binding.tvCategory.setTextColor(ContextCompat.getColor(binding.root.context, colorRes))

            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BloodPressure>() {
            override fun areItemsTheSame(a: BloodPressure, b: BloodPressure) = a.id == b.id
            override fun areContentsTheSame(a: BloodPressure, b: BloodPressure) = a == b
        }
    }
}
