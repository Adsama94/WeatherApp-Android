package com.adsama.weatherapp.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsama.model.Hour
import com.adsama.weatherapp.databinding.ItemHourlyBinding

class HourlyForecastAdapter : ListAdapter<Hour, HourlyForecastAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as Hour
        holder.bind(item)
    }

    class ViewHolder private constructor(private val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hour: Hour) {
            binding.hourlyData = hour
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHourlyBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Hour>() {
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem == newItem
        }
    }

}