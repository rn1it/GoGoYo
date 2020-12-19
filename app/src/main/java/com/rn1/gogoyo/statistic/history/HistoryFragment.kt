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

        viewModel.walks.observe(viewLifecycleOwner, Observer { list ->
            list?.let { it1 ->
                it1.sortedBy { it.endTime }
                for (i in it1) {
                    Logger.d("i = ${i.endTime}")
                }
                adapter.submitList(it1)
            }
        })

        return binding.root
    }
}