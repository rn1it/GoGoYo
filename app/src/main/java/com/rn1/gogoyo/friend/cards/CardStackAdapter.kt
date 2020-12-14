package com.rn1.gogoyo.friend.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemFriendCardBinding
import com.rn1.gogoyo.model.Users

class CardStackAdapter(val viewModel: FriendCardsViewModel) : ListAdapter<Users, RecyclerView.ViewHolder>(UserDiffCallback()) {

    class ViewHolder(val binding: ItemFriendCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: FriendCardsViewModel, user: Users) {
            binding.user = user
            binding.viewModel = viewModel
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemFriendCardBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val user = getItem(position)
        viewHolder.bind(viewModel, user)
    }

    class UserDiffCallback: DiffUtil.ItemCallback<Users>(){
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }

    }


}
