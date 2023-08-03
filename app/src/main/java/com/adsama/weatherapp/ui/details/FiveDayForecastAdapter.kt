package com.adsama.weatherapp.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsama.model.ForecastDay
import com.adsama.weatherapp.databinding.ItemForecastBinding

class FiveDayForecastAdapter : ListAdapter<ForecastDay, FiveDayForecastAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as ForecastDay
        holder.bind(item)
    }

    class ViewHolder private constructor(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forecastDay: ForecastDay) {
            binding.forecastDayData = forecastDay
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemForecastBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ForecastDay>() {
        override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem == newItem
        }
    }
}