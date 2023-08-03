package com.adsama.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsama.model.SearchResponse
import com.adsama.weatherapp.databinding.ItemSearchSuggestionBinding
import com.adsama.weatherapp.utils.ItemClickInterface

class SearchSuggestionsAdapter(itemClickInterface: ItemClickInterface.SearchSuggestionInterface) :
    ListAdapter<SearchResponse, SearchSuggestionsAdapter.ViewHolder>(DiffUtilCallback()) {

    private val mItemClickInterface = itemClickInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as SearchResponse
        holder.bind(item)
        holder.itemView.setOnClickListener {
            mItemClickInterface.getSelectedSuggestionClick(item, position)
        }
    }

    class ViewHolder private constructor(private val binding: ItemSearchSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(searchResponseItem: SearchResponse) {
            binding.searchResponseData = searchResponseItem
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSearchSuggestionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<SearchResponse>() {
        override fun areItemsTheSame(
            oldItem: SearchResponse,
            newItem: SearchResponse
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchResponse,
            newItem: SearchResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

}