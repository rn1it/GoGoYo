package com.rn1.gogoyo.friend.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemChatRoomBinding
import com.rn1.gogoyo.model.Chatroom

class FriendChatAdapter: ListAdapter<Chatroom, RecyclerView.ViewHolder>(ChatRoomDiffCallback) {

    class ChatRoomViewHolder(val binding: ItemChatRoomBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(chatRoom: Chatroom){
            binding.chatRoom = chatRoom
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ChatRoomViewHolder{
                return ChatRoomViewHolder(ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatRoomViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatRoomViewHolder = holder as ChatRoomViewHolder
        val chatRoom = getItem(position)
        chatRoomViewHolder.bind(chatRoom)
    }

    companion object ChatRoomDiffCallback: DiffUtil.ItemCallback<Chatroom>(){
        override fun areItemsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Chatroom, newItem: Chatroom): Boolean {
            return oldItem == newItem
        }

    }


}