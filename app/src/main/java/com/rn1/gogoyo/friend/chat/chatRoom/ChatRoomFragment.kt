package com.rn1.gogoyo.friend.chat.chatRoom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentChatRoomBinding
import com.rn1.gogoyo.databinding.FragmentFriendChatBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Messages
import com.rn1.gogoyo.model.Users

class ChatRoomFragment : Fragment() {

    private lateinit var binding: FragmentChatRoomBinding
    private val viewModel by viewModels<ChatRoomViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.msgRv
        val adapter = ChatRoomAdapter()

        recyclerView.adapter = adapter

        val user = Users("1", "user")
        val friend = Users("1", "friend")

        val msg1 = Messages("01", user, friend, "hi")
        val msg2 = Messages("02", friend, user, "hihi")
        val msg3 = Messages("01", user, friend, "hi")
        val msg4 = Messages("02", friend, user, "hihi")

        val list = mutableListOf<Messages>()
        list.add(msg1)
        list.add(msg2)
        list.add(msg3)
        list.add(msg4)
        adapter.separateMsgSubmitList(list)



        return binding.root
    }
}