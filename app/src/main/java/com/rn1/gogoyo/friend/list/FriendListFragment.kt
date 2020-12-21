package com.rn1.gogoyo.friend.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dev.materialspinner.MaterialSpinner
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendListBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.util.Logger
import java.util.*


class FriendListFragment(val userId: String) : Fragment() {

    private lateinit var binding: FragmentFriendListBinding
    private val viewModel by viewModels<FriendListViewModel> { getVmFactory(userId) }
    private var showChatBt = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.d("list onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_list, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val recyclerView = binding.friendListRv
        val adapter = FriendListAdapter(viewModel)
        recyclerView.adapter = adapter

        viewModel.friendList.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                    Logger.d("friendlist = $it")
                    binding.friendListSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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
                viewModel.toChatRoomDone()
            }
        })

        viewModel.navigateToProfile.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalMyPetsFragment(it))
                viewModel.toProfileDone()
            }
        })

        setUpSpinner()

        return binding.root
    }

    // return query list
    fun filter(list: List<Users>, query: String): List<Users> {

        val lowerCaseQueryString = query.toLowerCase(Locale.ROOT)
        val filteredList = mutableListOf<Users>()

        for (user in list) {
            val name = user.name.toLowerCase(Locale.ROOT)

            if (name.contains(lowerCaseQueryString)) {
                filteredList.add(user)
            }
        }

        return filteredList
    }

    private fun setUpSpinner(){
        binding.relationshipSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val friendShip = parent?.getItemAtPosition(position) as String

                when (friendShip) {
                    "朋友" -> showChatBt = true
                    "好友邀請" -> showChatBt = false
                    "等待中" -> showChatBt = false

                }

                viewModel.getUserFriends(friendShip)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Logger.d("list onViewCreated")
    }
}