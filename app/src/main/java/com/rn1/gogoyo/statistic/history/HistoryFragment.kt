package com.rn1.gogoyo.statistic.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentHistoryBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.Logger

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by viewModels<HistoryViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val recyclerView = binding.walkHistoryRv
        val adapter = HistoryAdapter()

        recyclerView.adapter = adapter

        viewModel.walks.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isEmpty()) {
                    binding.noHistoryDataTv.visibility = View.VISIBLE
                } else {
                    binding.noHistoryDataTv.visibility = View.GONE
                    it.sortedByDescending { walk -> walk.createdTime }
                    adapter.submitList(it)
                }
            }
        })

        return binding.root
    }
}