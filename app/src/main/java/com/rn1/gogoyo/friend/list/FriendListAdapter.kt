package com.rn1.gogoyo.friend.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemChatRoomBinding
import com.rn1.gogoyo.databinding.ItemFriendBinding
import com.rn1.gogoyo.model.Chatroom
import com.rn1.gogoyo.model.Friends

class FriendListAdapter: ListAdapter<Friends, RecyclerView.ViewHolder>(FriendDiffCallback) {

    class FriendViewHolder(val binding: ItemFriendBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(friends: Friends){
            binding.friend = friends
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): FriendViewHolder{
                return FriendViewHolder(ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FriendViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val friendViewHolder = holder as FriendViewHolder
        val friends = getItem(position)
        friendViewHolder.bind(friends)
    }

    companion object FriendDiffCallback: DiffUtil.ItemCallback<Friends>(){
        override fun areItemsTheSame(oldItem: Friends, newItem: Friends): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Friends, newItem: Friends): Boolean {
            return oldItem == newItem
        }

    }

}