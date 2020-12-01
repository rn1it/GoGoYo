package com.rn1.gogoyo.friend.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendListBinding
import com.rn1.gogoyo.model.Friends


class FriendListFragment : Fragment() {

    private lateinit var binding: FragmentFriendListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_list, container, false)
        binding.lifecycleOwner = this

        val recyclerView = binding.friendListRv
        val adapter = FriendListAdapter()

        recyclerView.adapter = adapter

        val f1 = Friends()
        val list = mutableListOf<Friends>()

        list.add(f1)
        list.add(f1)
        list.add(f1)
        list.add(f1)
        list.add(f1)
        list.add(f1)
        list.add(f1)

        adapter.submitList(list)

        return binding.root
    }
}