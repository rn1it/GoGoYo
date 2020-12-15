package com.rn1.gogoyo.friend.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.databinding.ItemFriendCardBinding
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.util.Logger


class CardStackAdapter(
    val viewModel: FriendCardsViewModel
) : ListAdapter<Users, RecyclerView.ViewHolder>(UserDiffCallback()) {

    class ViewHolder(val binding: ItemFriendCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: FriendCardsViewModel, user: Users, position: Int = 0) {

            var position = position

            binding.user = user
            binding.pet = user.pets[position]
            binding.viewModel = viewModel



            binding.backBt.setOnClickListener {
                if (position > 0) {
                    position -= 1
                    Logger.d("111111, ${user.pets[position].image}")
                    viewModel.dataChange(adapterPosition, position )
                }
            }



            binding.nextBt.setOnClickListener {
                if (position < user.pets.size - 1)
                    position += 1
                    Logger.d("111111, ${user.pets[position].image}")
                    viewModel.dataChange(adapterPosition, position )
                }
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

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            //payloads 為 空，說明是更新整個 ViewHolder
            onBindViewHolder(holder, position)
            Logger.d("111111, onBindViewHolder")
        } else {

            val viewHolder = holder as ViewHolder
            val user = getItem(position)
            viewHolder.bind(viewModel, user, payloads[0] as Int)
            Logger.d("2222222, onBindViewHolder ${payloads[0]}")
        }
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
