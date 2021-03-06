package com.rn1.gogoyo.friend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentFriendBinding
import com.rn1.gogoyo.friend.cards.FriendCardsFragment
import com.rn1.gogoyo.friend.chat.FriendChatFragment
import com.rn1.gogoyo.friend.list.FriendListFragment
import com.rn1.gogoyo.model.Articles

class FriendFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userId = requireArguments().getString("userIdKey")!!

        val binding = DataBindingUtil.inflate<FragmentFriendBinding>(inflater, R.layout.fragment_friend, container, false)

        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(FriendCardsFragment(userId))
        fragmentList.add(FriendListFragment(userId))
        fragmentList.add(FriendChatFragment(userId))

        val tabLayout = binding.friendTabLayout

        val pagerAdapter = FriendPagerAdapter(fragmentList, this)
        val viewPager = binding.friendViewPager

        // disable outer fragment slide
        viewPager.isUserInputEnabled = false
        viewPager.adapter = pagerAdapter


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "推薦"
                1 -> tab.text = "好友列表"
                2 -> tab.text = "聊天"
            }
            viewPager.currentItem = tab.position
        }.attach()

        return binding.root
    }

}