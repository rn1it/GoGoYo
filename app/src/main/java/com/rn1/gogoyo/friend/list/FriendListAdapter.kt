package com.rn1.gogoyo.friend.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.ItemFriendBinding
import com.rn1.gogoyo.databinding.ItemFriendStatusTitleBinding
import com.rn1.gogoyo.home.content.ArticleContentPetImageAdapter
import com.rn1.gogoyo.model.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TITLE = 0
private const val USER_FRIEND = 1
class FriendListAdapter(val viewModel: FriendListViewModel) : ListAdapter<FriendDataItem, RecyclerView.ViewHolder>(FriendDiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    class FriendStatusTitleViewHolder(val binding: ItemFriendStatusTitleBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(status: String) {
            binding.friendStatusTitleTv.text = status
        }

        companion object{
            fun from(parent: ViewGroup): FriendStatusTitleViewHolder{
                return FriendStatusTitleViewHolder(ItemFriendStatusTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

    }

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

    fun addStatusAndSubmitList(list: List<Users>){

        adapterScope.launch {

            val dataList = mutableListOf<FriendDataItem>()

            for (status in 0..2){
                when (status) {
                    0 -> dataList.add(FriendDataItem.Title("等待中"))
                    1 -> dataList.add(FriendDataItem.Title("好友邀請"))
                    2 -> dataList.add(FriendDataItem.Title("朋友"))
                }

                val userList = list.filter { it.status == status }
                for (user in userList) {
                    dataList.add(FriendDataItem.UserFriend(user))
                }
            }

            withContext(Dispatchers.Main){
                submitList(dataList)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            TITLE -> FriendStatusTitleViewHolder.from(parent)
            else -> FriendViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is FriendStatusTitleViewHolder -> {
                val itemTitle = getItem(position) as FriendDataItem.Title
                holder.bind(itemTitle.status)
            }
            is FriendViewHolder -> {
                val itemUser = getItem(position) as FriendDataItem.UserFriend
                holder.bind(viewModel, itemUser.user)
            }
        }



    }

    companion object FriendDiffCallback: DiffUtil.ItemCallback<FriendDataItem>(){
        override fun areItemsTheSame(oldItem: FriendDataItem, newItem: FriendDataItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FriendDataItem, newItem: FriendDataItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is FriendDataItem.Title -> TITLE
            else -> USER_FRIEND
        }
    }

}

sealed class FriendDataItem{

    data class Title(val status: String): FriendDataItem(){
        override var id: String = status
    }

    data class UserFriend(val user: Users): FriendDataItem(){
        override var id: String = user.id
    }

    abstract var id: String
}