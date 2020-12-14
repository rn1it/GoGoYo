package com.rn1.gogoyo.mypets.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentProfileUserBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Articles

class ProfileUserFragment(val userId: String) : Fragment() {

    private lateinit var binding: FragmentProfileUserBinding
    private val viewModel by viewModels<ProfileUserViewModel> { getVmFactory(userId) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_user, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val tabLayout = binding.profileArticleTabLayout

        val pagerAdapter = ProfileUserArticlePagerAdapter(viewModel)
        val viewPager = binding.profileArticleViewPager

        // disable outer fragment slide
        viewPager.isUserInputEnabled = false
        viewPager.adapter = pagerAdapter

        // Default no value for viewPager list
        val viewPagerList = mutableListOf<List<Articles>>()
        viewPagerList.add(0, mutableListOf() )
        viewPagerList.add(1, mutableListOf() )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "我的動態"
                1 -> tab.text = "收藏"
            }
            viewPager.currentItem = tab.position
        }.attach()

//        viewModel.loginUser.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                viewModel.getFriendStatus()
//            }
//        })

        viewModel.loginUserFriends.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getFriendStatus()
            }
        })

        viewModel.userArticles.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewPagerList[0] = it
                pagerAdapter.submitList(viewPagerList)
            }
        })

        viewModel.userFavArticles.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewPagerList[1] = it
                pagerAdapter.submitList(viewPagerList)
            }
        })

        viewModel.navigateToContent.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalArticleContentFragment(it))
                viewModel.onDoneNavigateToContent()
            }
        })

        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalEditUserFragment(it))
                viewModel.onDoneNavigateToEdit()
            }
        })

        return binding.root
    }


}