package com.rn1.gogoyo.home.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemResponseBinding
import com.rn1.gogoyo.model.ArticleResponse

class ArticleResponseAdapter: ListAdapter<ArticleResponse, RecyclerView.ViewHolder>(ArticleResponseDiffCallback) {

    class ArticleResponseViewHolder(val binding: ItemResponseBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(articleResponse: ArticleResponse){
            binding.response = articleResponse
            binding.executePendingBindings()
            binding.imageView14.outlineProvider = MapOutlineProvider()

        }

        companion object{
            fun from(parent: ViewGroup): ArticleResponseViewHolder{
                return ArticleResponseViewHolder(ItemResponseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleResponseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val articleResponseViewHolder = holder as ArticleResponseViewHolder
        val articleResponse = getItem(position)
        articleResponseViewHolder.bind(articleResponse)
    }

    companion object ArticleResponseDiffCallback: DiffUtil.ItemCallback<ArticleResponse>(){
        override fun areItemsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ArticleResponse, newItem: ArticleResponse): Boolean {
            return oldItem == newItem
        }

    }

}