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
            binding.walk = walk
            val adapter = ArticleContentPetImageAdapter()
            binding.petsImageRv.adapter = adapter
            adapter.submitList(walk.pets)
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

    private fun formatTime(second: Int): String{

        val hour = second / 3600
        var secondTime = second % 3600
        val minute = secondTime / 60
        secondTime %= 60

        return "${addZero(hour)}時${addZero(minute)}分${addZero(secondTime)}秒"
    }

    private fun addZero(number: Int): String{
        return if(number.toString().length == 1){
            "0$number"
        } else {
            "$number"
        }
    }
}