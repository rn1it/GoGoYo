package com.rn1.gogoyo.mypets.newpets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentNewPetBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_INTRODUCTION_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_NAME_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_SEX_EMPTY

class NewPetFragment : Fragment() {

    private lateinit var binding: FragmentNewPetBinding
    private val viewModel by viewModels<NewPetViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_pet, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        viewModel.canAddPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) viewModel.addNewPet()
            }
        })

        viewModel.invalidInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    INVALID_FORMAT_NAME_EMPTY -> Toast.makeText(context, "請輸入寵物姓名!", Toast.LENGTH_SHORT).show()
                    INVALID_FORMAT_INTRODUCTION_EMPTY -> Toast.makeText(context, "來段簡短的介紹讓大家更認識你!", Toast.LENGTH_SHORT).show()
                    INVALID_FORMAT_SEX_EMPTY ->  Toast.makeText(context, "記得選擇寵物性別喔!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigateToPets.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(context, "成員新增成功!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(NavigationDirections.actionGlobalMyPetsFragment())
                }
                viewModel.onDoneNavigateToPet()
            }
        })

        return binding.root
    }
}