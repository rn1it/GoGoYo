package com.rn1.gogoyo.mypets

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentMyPetsBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.friend.FriendPagerAdapter
import com.rn1.gogoyo.friend.cards.FriendCardsFragment
import com.rn1.gogoyo.friend.chat.FriendChatFragment
import com.rn1.gogoyo.friend.list.FriendListFragment
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.mypets.pet.MyPetsPagerAdapter
import com.rn1.gogoyo.mypets.pet.ProfilePetFragment
import com.rn1.gogoyo.mypets.user.ProfileUserFragment
import kotlin.math.abs

class MyPetsFragment : Fragment() {

    private lateinit var binding: FragmentMyPetsBinding
    private val viewModel by viewModels<MyPetsViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_pets, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(ProfilePetFragment())
        fragmentList.add(ProfileUserFragment())

        val tabLayout = binding.profileTabLayout

        val pagerAdapter = ProfilePagerAdapter(fragmentList, this)
        val viewPager = binding.profileViewPager

        // disable outer fragment slide
        viewPager.isUserInputEnabled = false
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "寵物"
                1 -> tab.text = "個人"
            }
            viewPager.currentItem = tab.position
        }.attach()

        return binding.root
    }
}