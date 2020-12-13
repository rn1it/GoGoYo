package com.rn1.gogoyo.friend.chat.chatRoom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentChatRoomBinding
import com.rn1.gogoyo.databinding.FragmentFriendChatBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Messages
import com.rn1.gogoyo.model.Users

class ChatRoomFragment : Fragment() {

    private lateinit var binding: FragmentChatRoomBinding
    private val viewModel by viewModels<ChatRoomViewModel> { getVmFactory( ChatRoomFragmentArgs.fromBundle(requireArguments()).chatRoomKey) }

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

        viewModel.liveMessages.observe(viewLifecycleOwner, Observer {

            it?.let {
                adapter.separateMsgSubmitList(it)
            }
        })


        viewModel.clearMsg.observe(viewLifecycleOwner, Observer {

            it?.let {
                if (it) {
                    binding.msgEt.text = null
                    viewModel.content.value = null
                }
            }
        })



        return binding.root
    }
}