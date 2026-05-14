package com.healthfit.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.healthfit.data.entities.PhysicalActivity
import com.healthfit.databinding.ItemActivityBinding

class ActivityAdapter(
    private val onDelete: (PhysicalActivity) -> Unit
) : ListAdapter<PhysicalActivity, ActivityAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhysicalActivity) {
            val icon = when (item.activityType.name) {
                "WALKING" -> "🚶"
                "RUNNING" -> "🏃"
                "CYCLING" -> "🚴"
                "SWIMMING" -> "🏊"
                "YOGA"    -> "🧘"
                "GYM"     -> "🏋️"
                "HIKING"  -> "🥾"
                "DANCING" -> "💃"
                else      -> "⚡"
            }
            binding.tvActivityType.text = "$icon ${item.activityType.name.replace("_", " ")}"
            binding.tvDuration.text = "${item.durationMinutes} min"
            binding.tvCalories.text = "${item.caloriesBurned.toInt()} kcal"
            if (item.distanceKm > 0) binding.tvDistance.text = String.format("%.2f km", item.distanceKm)
            if (item.steps > 0) binding.tvSteps.text = "${item.steps} steps"
            binding.tvDateTime.text = "${item.date}  ${item.time}"
            if (item.notes.isNotBlank()) binding.tvNotes.text = item.notes
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PhysicalActivity>() {
            override fun areItemsTheSame(a: PhysicalActivity, b: PhysicalActivity) = a.id == b.id
            override fun areContentsTheSame(a: PhysicalActivity, b: PhysicalActivity) = a == b
        }
    }
}
