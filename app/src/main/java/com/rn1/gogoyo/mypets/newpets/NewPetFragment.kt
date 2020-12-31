package com.rn1.gogoyo.mypets.newpets

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.databinding.FragmentNewPetBinding
import com.rn1.gogoyo.ext.checkPermission
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_INTRODUCTION_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_NAME_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_SEX_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_IMAGE_PATH_EMPTY
import com.rn1.gogoyo.util.Logger
import java.io.File


class NewPetFragment : Fragment() {

    private lateinit var binding: FragmentNewPetBinding
    private val viewModel by viewModels<NewPetViewModel> { getVmFactory() }
    private var filePath: String = ""


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
                    INVALID_IMAGE_PATH_EMPTY -> Toast.makeText(context, "別忘記上傳一張寵物的萌照啊!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigateToPets.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(context, "成員新增成功!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(NavigationDirections.actionGlobalMyPetsFragment(UserManager.userUID!!))
                }
                viewModel.onDoneNavigateToPet()
            }
        })

        binding.uploadPetIv.setOnClickListener{
            checkPermission()
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Logger.d("Permission denied")
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {

            Activity.RESULT_OK -> {
                filePath = ImagePicker.getFilePath(data) ?: ""
                if (filePath.isNotEmpty()) {
                    val imgPath = filePath
                    Glide.with(this.requireContext()).load(filePath).into(binding.uploadPetIv)

                    viewModel.uploadImage(imgPath)

                } else {
                    Toast.makeText(this.requireContext(), "上傳失敗", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            ImagePicker.RESULT_ERROR -> Logger.d(ImagePicker.getError(data))

            else -> Toast.makeText(this.requireContext(), "上傳失敗", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 200
    }
}