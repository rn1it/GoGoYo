package com.rn1.gogoyo.statistic.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemWalkBinding
import com.rn1.gogoyo.home.content.ArticleContentPetImageAdapter
import com.rn1.gogoyo.model.Walk

class HistoryAdapter: ListAdapter<Walk, HistoryAdapter.WalkViewHolder>(WalkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkViewHolder {
        return WalkViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WalkViewHolder, position: Int) {
        val walk = getItem(position)
        holder.bind(walk)
    }

    class WalkViewHolder(val binding: ItemWalkBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(walk: Walk){
            val adapter = ArticleContentPetImageAdapter()
            adapter.submitList(walk.pets)

            binding.walk = walk
            binding.petsImageRv.adapter = adapter
        }

        companion object{
            fun from(parent: ViewGroup): WalkViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemWalkBinding.inflate(inflater, parent, false)
                return WalkViewHolder(binding)
            }
        }
    }

    class WalkDiffCallback: DiffUtil.ItemCallback<Walk>(){
        override fun areItemsTheSame(oldItem: Walk, newItem: Walk): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Walk, newItem: Walk): Boolean {
            return oldItem == newItem
        }

    }
}