package com.rn1.gogoyo.home.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemArticleImageBinding

class ArticleContentAdapter: ListAdapter<String, RecyclerView.ViewHolder>(ArticleImageDiffCallback) {

    class ArticleImageViewHolder(val binding: ItemArticleImageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(string: String){
            binding.imgurl = string
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ArticleImageViewHolder{
                return ArticleImageViewHolder(ItemArticleImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val articleImageViewHolder = holder as ArticleImageViewHolder
        val string = getItem(position)
        articleImageViewHolder.bind(string)
    }

    companion object ArticleImageDiffCallback: DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

}