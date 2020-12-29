package com.rn1.gogoyo.friend.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.ItemFriendBinding
import com.rn1.gogoyo.home.content.ArticleContentPetImageAdapter
import com.rn1.gogoyo.model.Users

class FriendListAdapter(val viewModel: FriendListViewModel) : ListAdapter<Users, RecyclerView.ViewHolder>(FriendDiffCallback) {

    class FriendViewHolder(val binding: ItemFriendBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(viewModel: FriendListViewModel, user: Users){
            binding.user = user
            binding.viewModel = viewModel

            when (user.status) {
                1 -> {
                    binding.friendListBt.visibility = View.VISIBLE
                    binding.friendListBt.setImageResource(R.drawable.add_user)
                    binding.friendListBt.setOnClickListener {
                        viewModel.onClickProfileBt(user)
                    }
                }
                2 -> {
                    binding.friendListBt.visibility = View.VISIBLE
                    binding.friendListBt.setImageResource(R.drawable.chat)
                    binding.friendListBt.setOnClickListener {
                        viewModel.toChatRoom(user)
                    }
                }
                else -> {
                    binding.friendListBt.visibility = View.GONE
                }
            }



            val adapter = ArticleContentPetImageAdapter()
            binding.friendPetRecyclerView.adapter = adapter
            adapter.submitList(user.pets)


            binding.userImgIv.setOnClickListener {
                viewModel.toProfile(user.id)
            }

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
        val user = getItem(position)
        friendViewHolder.bind(viewModel, user)
    }

    companion object FriendDiffCallback: DiffUtil.ItemCallback<Users>(){
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }

    }

}