package com.rn1.gogoyo.friend.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemFriendCardBinding
import com.rn1.gogoyo.model.Users

class FriendCardsAdapter: ListAdapter<Users, RecyclerView.ViewHolder>(UserDiffCallback) {

    class UserViewHolder(val binding: ItemFriendCardBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(users: Users){
            binding.users = users
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): UserViewHolder{
                return UserViewHolder(ItemFriendCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userViewHolder = holder as UserViewHolder
        val users = getItem(position)
        userViewHolder.bind(users)
    }


    companion object UserDiffCallback: DiffUtil.ItemCallback<Users>() {
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }
    }

}