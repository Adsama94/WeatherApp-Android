package com.adsama.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsama.database.PersistedWeatherModel
import com.adsama.weatherapp.databinding.ItemSearchResultBinding
import com.adsama.weatherapp.utils.ItemClickInterface

class SavedLocationResultsAdapter(itemClickInterface: ItemClickInterface.SelectedLocationInterface) :
    ListAdapter<PersistedWeatherModel, SavedLocationResultsAdapter.ViewHolder>(DiffUtilCallback()) {

    private val mItemClickInterface = itemClickInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as PersistedWeatherModel
        holder.bind(item)
        holder.itemView.setOnClickListener {
            mItemClickInterface.getSelectedLocationClick(item, position)
        }
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

    class ViewHolder private constructor(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(persistedWeatherModel: PersistedWeatherModel) {
            binding.persistedWeatherModel = persistedWeatherModel
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchResultBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<PersistedWeatherModel>() {
        override fun areItemsTheSame(
            oldItem: PersistedWeatherModel,
            newItem: PersistedWeatherModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PersistedWeatherModel,
            newItem: PersistedWeatherModel
        ): Boolean {
            return oldItem == newItem
        }
    }

}