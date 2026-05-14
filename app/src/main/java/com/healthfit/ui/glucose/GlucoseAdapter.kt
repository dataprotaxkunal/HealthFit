package com.healthfit.ui.glucose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.healthfit.R
import com.healthfit.data.entities.BloodGlucose
import com.healthfit.data.entities.GlucoseStatus
import com.healthfit.databinding.ItemGlucoseBinding

class GlucoseAdapter(
    private val onDelete: (BloodGlucose) -> Unit
) : ListAdapter<BloodGlucose, GlucoseAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGlucoseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemGlucoseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BloodGlucose) {
            binding.tvGlucoseLevel.text = "${item.glucoseLevel.toInt()} mg/dL"
            binding.tvMealType.text = item.mealType.name.replace("_", " ")
            binding.tvDateTime.text = "${item.date}  ${item.time}"
            if (item.notes.isNotBlank()) binding.tvNotes.text = item.notes

            val status = item.getStatus()
            val (statusText, color) = when (status) {
                GlucoseStatus.NORMAL     -> "Normal" to R.color.status_normal
                GlucoseStatus.LOW        -> "Low" to R.color.status_low
                GlucoseStatus.PRE_DIABETIC -> "Pre-Diabetic" to R.color.status_warning
                GlucoseStatus.HIGH       -> "High" to R.color.status_high
            }
            binding.tvStatus.text = statusText
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(binding.root.context, color)
            )

            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BloodGlucose>() {
            override fun areItemsTheSame(a: BloodGlucose, b: BloodGlucose) = a.id == b.id
            override fun areContentsTheSame(a: BloodGlucose, b: BloodGlucose) = a == b
        }
    }
}
