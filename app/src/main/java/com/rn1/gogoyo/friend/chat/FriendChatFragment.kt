package com.rn1.gogoyo.friend.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendChatBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Chatroom
import java.util.*


class FriendChatFragment(val userId: String) : Fragment() {

    private lateinit var binding: FragmentFriendChatBinding
    private val viewModel by viewModels<FriendChatViewModel> { getVmFactory(userId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_chat, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.chatListRv
        val adapter = FriendChatAdapter(FriendChatAdapter.OnClickListener{
            viewModel.navigateToChatRoom(it)
        })

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        viewModel.liveChatRoomList.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getChatRoomWithFriendInfo(it)
            }
        })

        viewModel.chatList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

                binding.friendChatListSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false

                    override fun onQueryTextChange(query: String): Boolean {
                        adapter.submitList(filter(it, query))
                        return true
                    }
                })
            }
        })

        viewModel.navigateToChatRoom.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalChatRoomFragment(it))
                viewModel.onDoneNavigateToChatRoom()
            }
        })

        return binding.root
    }

    fun filter(list: List<Chatroom>, query: String): List<Chatroom> {

        val lowerCaseQueryString = query.toLowerCase(Locale.ROOT)
        val filteredList = mutableListOf<Chatroom>()

        for (chatRoom in list) {
            val name = chatRoom.friend!!.name.toLowerCase(Locale.ROOT)
            val lastMsg = chatRoom.lastMsg ?: "".toLowerCase(Locale.ROOT)
            if (lastMsg.contains(lowerCaseQueryString) || name.contains(lowerCaseQueryString)) {
                filteredList.add(chatRoom)
            }
        }

        return filteredList
    }
}