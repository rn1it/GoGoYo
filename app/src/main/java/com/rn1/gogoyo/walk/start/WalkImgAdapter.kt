package com.rn1.gogoyo.walk.start

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemWalkImageBinding

class WalkImgAdapter: ListAdapter<String, WalkImgAdapter.ViewHolder>(UriDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = getItem(position)
        holder.bind(uri)
    }

    class ViewHolder(val binding: ItemWalkImageBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(uri: String){
            binding.imgUrl = uri
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemWalkImageBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class UriDiffCallback: DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }



}