package com.rn1.gogoyo.walk.end

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkEndBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Pets

class WalkEndFragment : Fragment() {

    private lateinit var binding:FragmentWalkEndBinding
    private val viewModel by viewModels<WalkEndViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk_end, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.walkEndPetRv
        val adapter = PetAdapter()

        recyclerView.adapter = adapter

        val pet1 = Pets("001","111")
        val list = mutableListOf<Pets>()
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)

        adapter.submitList(list)
        // Inflate the layout for this fragment
        return binding.root
    }
}