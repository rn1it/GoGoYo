package com.rn1.gogoyo.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemArticleBinding
import com.rn1.gogoyo.model.Articles

class HomeAdapter(private val onClickListener: OnClickListener): ListAdapter<Articles, RecyclerView.ViewHolder>(ArticleDiffCallback) {

    class ArticleViewHolder(val binding: ItemArticleBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(articles: Articles){
            binding.article = articles
            binding.itemAuthorIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ArticleViewHolder{
                return ArticleViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val articleViewHolder = holder as ArticleViewHolder
        val article = getItem(position)

        articleViewHolder.itemView.setOnClickListener {
                onClickListener.onClick(article)
        }

        articleViewHolder.bind(article)

    }

    companion object ArticleDiffCallback: DiffUtil.ItemCallback<Articles>(){
        override fun areItemsTheSame(oldItem: Articles, newItem: Articles): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Articles, newItem: Articles): Boolean {
            return oldItem == newItem
        }

    }

    class OnClickListener(val clickListener: (articles: Articles) -> Unit) {
        fun onClick(articles: Articles) = clickListener(articles)
    }

}