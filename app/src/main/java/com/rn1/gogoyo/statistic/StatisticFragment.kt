package com.rn1.gogoyo.statistic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentStatisticBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.statistic.history.HistoryFragment
import com.rn1.gogoyo.statistic.total.TotalWalkFragment

class StatisticFragment : Fragment() {

    private val viewModel by viewModels<StatisticViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentStatisticBinding>(inflater, R.layout.fragment_statistic, container, false)

        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(HistoryFragment())
        fragmentList.add(TotalWalkFragment())


        val tabLayout = binding.statisticTabLayout
        val pagerAdapter = StatisticAdapter(fragmentList, this)
        val viewPager = binding.statisticViewPager

        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "紀錄"
                1 -> tab.text = "統計"
            }
            viewPager.currentItem = tab.position
        }.attach()

        return binding.root
    }
}