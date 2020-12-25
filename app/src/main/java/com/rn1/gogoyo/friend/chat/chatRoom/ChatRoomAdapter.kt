package com.rn1.gogoyo.friend.chat.chatRoom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemMsgFriendBinding
import com.rn1.gogoyo.databinding.ItemMsgUserBinding
import com.rn1.gogoyo.model.Messages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private const val MSG_FROM_USER = 0
private const val MSG_FROM_FRIEND = 1
class ChatRoomAdapter: ListAdapter<DataItem, RecyclerView.ViewHolder>(MessageDiffCallBack) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            MSG_FROM_USER -> {
                MsgUserViewHolder.from(parent)
            }
            MSG_FROM_FRIEND -> {
                MsgFriendViewHolder.from(parent)
            }
            else -> throw ClassCastException("Unknown ViewType: $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is MsgUserViewHolder -> {
                val msgItem = getItem(position) as DataItem.UserMsg
                holder.bind(msgItem.messages)
            }
            is MsgFriendViewHolder -> {
                val msgItem = getItem(position) as DataItem.FriendMsg
                holder.bind(msgItem.messages)
            }
        }

    }

    class MsgUserViewHolder(val binding: ItemMsgUserBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(msg: Messages){
            binding.msg = msg
        }

        companion object {
            fun from(parent: ViewGroup): MsgUserViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMsgUserBinding.inflate(layoutInflater, parent, false)

                return MsgUserViewHolder(binding)
            }
        }
    }

    class MsgFriendViewHolder(val binding: ItemMsgFriendBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(msg: Messages){
            binding.msg = msg
            binding.imageView13.outlineProvider = MapOutlineProvider()
        }

        companion object {
            fun from(parent: ViewGroup): MsgFriendViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMsgFriendBinding.inflate(layoutInflater, parent, false)

                return MsgFriendViewHolder(binding)
            }
        }
    }

    fun separateMsgSubmitList(list: List<Messages>){
        adapterScope.launch {
            val itemList = mutableListOf<DataItem>()
            list.let {
                for (msg in it) {
                    if (msg.senderId == UserManager.userUID) {
                        itemList.add(DataItem.UserMsg(msg))
                    } else {
                        itemList.add(DataItem.FriendMsg(msg))
                    }
                }
            }

            withContext(Dispatchers.Main){
                submitList(itemList)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        return when(getItem(position)) {
            is DataItem.UserMsg -> MSG_FROM_USER
            else -> MSG_FROM_FRIEND
        }
    }


    companion object MessageDiffCallBack: DiffUtil.ItemCallback<DataItem>(){

        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }
}

sealed class DataItem{

    data class UserMsg(val messages: Messages): DataItem(){
        override var id: String = messages.id
    }

    data class FriendMsg(val messages: Messages): DataItem(){
        override var id: String = messages.id
    }
    abstract var id: String
}