package com.rn1.gogoyo.mypets.pet

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
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentMyPetsBinding
import com.rn1.gogoyo.databinding.FragmentProfilePetBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.mypets.MyPetsViewModel
import kotlin.math.abs

class ProfilePetFragment : Fragment() {

    private lateinit var binding: FragmentProfilePetBinding
    private val viewModel by viewModels<ProfilePetViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_pet, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel



        viewModel.onEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                petInfoEditableChange(true)
                viewModel.onDoneEdit()
            }
        })

        viewModel.onSureEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                petInfoEditableChange(false)
                viewModel.onDoneSureEdit()
            }
        })

        viewModel.onCancelEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                petInfoEditableChange(false)
                viewModel.onDoneCancelEdit()
            }
        })


        // mock data
        val pet1 = Pets("001", "ppp")
        val pet2 = Pets("002", "qqq")
        val pet3 = Pets("003", "aaa")
        val pet4 = Pets("004", "vvv")
        val pet5 = Pets("005", "fff")

        val list = mutableListOf<Pets>()
        list.add(pet1)
        list.add(pet2)
        list.add(pet3)
        list.add(pet4)
        list.add(pet5)

        val viewPager = binding.myPetsViewPager


        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3

        // not to show slide to end effect
        viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER


        // listen page change and change data for selected pet
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.breedTitleEt.setText(list[position].name)
                Log.d("onPageSelected", "this is : $position")
            }
        })

        val adapter = MyPetsPagerAdapter()
        adapter.submitList(list)

        viewPager.adapter = adapter

        val transformer = CompositePageTransformer()
//        transformer.addTransformer(MarginPageTransformer(8))
        transformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val v = 1 - abs(position)
            page.scaleY = 0.6f + v * 0.4f
        })

        viewPager.setPageTransformer(transformer)

        return binding.root
    }

    private fun petInfoEditableChange(boolean: Boolean) {
        binding.breedTitleEt.apply {
            isFocusable = boolean
            isFocusableInTouchMode = boolean
        }
        binding.sexTitleEt.apply {
            isFocusable = boolean
            isFocusableInTouchMode = boolean
        }
        binding.birthTitleEt.apply {
            isFocusable = boolean
            isFocusableInTouchMode = boolean
        }
        binding.introductionTitleEt.apply {
            isFocusable = boolean
            isFocusableInTouchMode = boolean
        }
        if (boolean) {
            binding.sureEditBt.visibility = View.VISIBLE
            binding.cancelEditBt.visibility = View.VISIBLE
            binding.editPetInfoBt.visibility = View.GONE
        } else {
            binding.sureEditBt.visibility = View.GONE
            binding.cancelEditBt.visibility = View.GONE
            binding.editPetInfoBt.visibility = View.VISIBLE
        }
    }
}