package com.rn1.gogoyo.home.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentPostBinding
import com.rn1.gogoyo.model.Pets

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        binding.lifecycleOwner = this

        val pet1 = Pets("001","111")
        val list = mutableListOf<Pets>()
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)

        val recyclerView = binding.petsImageRv
        val adapter = PostPetAdapter()

        recyclerView.adapter = adapter

        adapter.submitList(list)

        // Inflate the layout for this fragment
        return binding.root
    }

}