package com.rn1.gogoyo.friend.cards

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.ItemFriendCardBinding
import com.rn1.gogoyo.model.Users

class CardStackAdapter(
    val viewModel: FriendCardsViewModel,
    private var users: List<Users> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFriendCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: FriendCardsViewModel, user: Users, currentPosition: Int = 0) {

            var position = currentPosition

            binding.user = user
            if (user.pets.isNotEmpty()) {
                binding.pet = user.pets[position]
                binding.viewModel = viewModel

                binding.dogCardCv.setOnClickListener {
                    viewModel.setShowBarkToast(user.pets[position])
                }

                binding.playVideoFab.setOnClickListener {
                    val pet = user.pets[position]
                    if (pet.video.isNullOrEmpty()) {
                        Toast.makeText(GogoyoApplication.instance.applicationContext
                            , GogoyoApplication.instance.getString(R.string.no_pet_video_text)
                            , Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.setShowVideoDialog(user.pets[position])
                    }
                }

                binding.backBt.setOnClickListener {
                    if (position > 0) {
                        position -= 1
                        viewModel.dataChange(adapterPosition, position )
                    }
                }

                binding.nextBt.setOnClickListener {
                    if (position < user.pets.size - 1)
                        position += 1
                    viewModel.dataChange(adapterPosition, position )
                }
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user = users[position]

        holder.bind(viewModel, user)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val viewHolder = holder as ViewHolder
            val user = users[position]
            viewHolder.bind(viewModel, user, payloads[0] as Int)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(users: List<Users>){
        this.users = users
    }

    fun getUsers(): List<Users>{
        return users
    }

    class UserDiffCallBack(
        private val old: List<Users>,
        private val new: List<Users>
    ): DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return old.size
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].id == new[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

    }
}
