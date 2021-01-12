package com.rn1.gogoyo.mypets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentMyPetsBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.pet.ProfilePetFragment
import com.rn1.gogoyo.mypets.user.ProfileUserFragment

class MyPetsFragment : Fragment() {

    private lateinit var binding: FragmentMyPetsBinding
    private val viewModel by viewModels<MyPetsViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val userId = MyPetsFragmentArgs.fromBundle(requireArguments()).userIdKey

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_pets, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(ProfilePetFragment(userId))
        fragmentList.add(ProfileUserFragment(userId))

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