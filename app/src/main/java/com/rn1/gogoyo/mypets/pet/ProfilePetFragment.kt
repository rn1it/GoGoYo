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
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentProfilePetBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.Logger
import kotlin.math.abs

class ProfilePetFragment : Fragment() {

    private lateinit var binding: FragmentProfilePetBinding
    private val viewModel by viewModels<ProfilePetViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_pet, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val viewPager = binding.myPetsViewPager

        val adapter = MyPetsPagerAdapter()
        viewPager.adapter = adapter

        viewModel.petList.observe(viewLifecycleOwner, Observer {
            it?.let {

                adapter.submitList(it)
                // listen page change and change data for selected pet
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        viewModel.pet.value = it[position]
                    }
                })
            }
        })

        viewModel.navigateToNewPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) findNavController().navigate(NavigationDirections.actionGlobalNewPetFragment())
                viewModel.onDoneNavigateToNewPet()
            }
        })

        viewModel.editPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalEditPetFragment(it))
                viewModel.toEditPetDone()
            }
        })

//        viewModel.onEdit.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                petInfoEditableChange(true)
//                viewModel.onDoneEdit()
//            }
//        })
//
//        viewModel.onSureEdit.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                petInfoEditableChange(false)
//                viewModel.onDoneSureEdit()
//            }
//        })
//
//        viewModel.onCancelEdit.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                petInfoEditableChange(false)
//                viewModel.onDoneCancelEdit()
//            }
//        })

        setUpViewPager(viewPager)

        return binding.root
    }

    private fun setUpViewPager(viewPager: ViewPager2){
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        // not to show slide to end effect
        viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        val transformer = CompositePageTransformer()
        //transformer.addTransformer(MarginPageTransformer(8))
        transformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val v = 1 - abs(position)
            page.scaleY = 0.6f + v * 0.4f
            page.scaleX = 0.6f + v * 0.4f
        })

        viewPager.setPageTransformer(transformer)
    }

//    private fun petInfoEditableChange(boolean: Boolean) {
//        binding.breedTitleEt.apply {
//            isFocusable = boolean
//            isFocusableInTouchMode = boolean
//        }
//        binding.sexTitleEt.apply {
//            isFocusable = boolean
//            isFocusableInTouchMode = boolean
//        }
//        binding.birthTitleEt.apply {
//            isFocusable = boolean
//            isFocusableInTouchMode = boolean
//        }
//        binding.introductionTitleEt.apply {
//            isFocusable = boolean
//            isFocusableInTouchMode = boolean
//        }
//        if (boolean) {
//            binding.sureEditBt.visibility = View.VISIBLE
//            binding.cancelEditBt.visibility = View.VISIBLE
//            binding.editPetInfoBt.visibility = View.GONE
//        } else {
//            binding.sureEditBt.visibility = View.GONE
//            binding.cancelEditBt.visibility = View.GONE
//            binding.editPetInfoBt.visibility = View.VISIBLE
//        }
//    }
}