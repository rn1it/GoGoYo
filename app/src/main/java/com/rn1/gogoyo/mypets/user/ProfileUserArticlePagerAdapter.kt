package com.rn1.gogoyo.mypets.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.databinding.ItemListLayoutBinding
import com.rn1.gogoyo.home.HomeViewModel
import com.rn1.gogoyo.model.Articles

class ProfileUserArticlePagerAdapter(val viewModel: ProfileUserViewModel) : ListAdapter<List<Articles>, RecyclerView.ViewHolder>(ListDiffCallback) {

    class ArticleListViewHolder(private val binding: ItemListLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(viewModel: ProfileUserViewModel, list: List<Articles>) {
            val adapter = UserArticleAdapter(UserArticleAdapter.OnClickListener{
                viewModel.navigateToContent(it)
            })
            binding.list = list
            binding.articleGridRv.adapter = adapter
            adapter.submitList(list)

            binding.articleGridRv.layoutManager = GridLayoutManager(GogoyoApplication.instance, 3)
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): ArticleListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListLayoutBinding.inflate(layoutInflater, parent, false)
                return ArticleListViewHolder(
                    binding
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArticleListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val articleListViewHolder = holder as ArticleListViewHolder
        val list = getItem(position)
        articleListViewHolder.bind(viewModel, list)
    }

    companion object ListDiffCallback: DiffUtil.ItemCallback<List<Articles>>(){
        override fun areItemsTheSame(oldItem: List<Articles>, newItem: List<Articles>): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: List<Articles>, newItem: List<Articles>): Boolean {
            return oldItem == newItem
        }

    }
}