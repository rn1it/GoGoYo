package com.rn1.gogoyo.mypets.edit

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
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.databinding.FragmentEditUserBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel

class EditUserFragment : Fragment() {

    private lateinit var binding: FragmentEditUserBinding
    private val viewModel by viewModels<EditUserViewModel> { getVmFactory(EditUserFragmentArgs.fromBundle(requireArguments()).userIdKey) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_user, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.canEditUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) viewModel.editUser()
            }
        })

        viewModel.invalidInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    NewPetViewModel.INVALID_FORMAT_NAME_EMPTY -> Toast.makeText(context, "寵物姓名不可以空白!", Toast.LENGTH_SHORT).show()
                    NewPetViewModel.INVALID_FORMAT_INTRODUCTION_EMPTY -> Toast.makeText(context, "來段簡短的介紹讓大家更認識你!", Toast.LENGTH_SHORT).show()
                    NewPetViewModel.INVALID_FORMAT_SEX_EMPTY ->  Toast.makeText(context, "記得選擇寵物性別喔!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigateToProfileUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(context, "個人資料修改成功!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(NavigationDirections.actionGlobalMyPetsFragment(UserManager.userUID!!))
                }
                viewModel.onDoneNavigateToUser()
            }
        })

        return binding.root
    }
}