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
import com.rn1.gogoyo.model.Friends
import java.util.*


class FriendChatFragment : Fragment() {

    private lateinit var binding: FragmentFriendChatBinding
    private val viewModel by viewModels<FriendChatViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_chat, container, false)
        binding.lifecycleOwner = this

        val recyclerView = binding.chatListRv
        val adapter = FriendChatAdapter(FriendChatAdapter.OnClickListener{
            viewModel.navigateToChatRoom()
        })

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

//        val chat1 = Chatroom("001", "dog1", "dog2", lastMsg = "回我訊息")
//        val chat2 = Chatroom("002", "dog2", "dog3", lastMsg = "你: 理我一下")
//        val chat3 = Chatroom("003", "dog3", "dog4", lastMsg = "你: 拜託")

        val list = mutableListOf<Chatroom>()
//        list.add(chat1)
//        list.add(chat2)
//        list.add(chat3)

        adapter.submitList(list)


    //TODO
//        viewModel.navigateToChatRoom.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                findNavController().navigate(NavigationDirections.actionGlobalChatRoomFragment())
//                viewModel.onDoneNavigateToChatRoom()
//            }
//        })

        binding.friendChatListSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(query: String): Boolean {
                adapter.submitList(filter(list, query))
                return true
            }
        })

        return binding.root
    }

    // return query list
    fun filter(list: List<Chatroom>, query: String): List<Chatroom> {

        val lowerCaseQueryString = query.toLowerCase(Locale.ROOT)
        val filteredList = mutableListOf<Chatroom>()

//        for (chatRoom in list) {
//            val name = chatRoom.user1Id.toLowerCase(Locale.ROOT)
//            val lastMsg = chatRoom.lastMsg ?: "".toLowerCase(Locale.ROOT)
//            if (name.contains(lowerCaseQueryString)) {
//                filteredList.add(chatRoom)
//            }
//        }

        return filteredList
    }
}