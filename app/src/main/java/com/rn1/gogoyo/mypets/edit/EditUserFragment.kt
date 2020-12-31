package com.rn1.gogoyo.mypets.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.databinding.FragmentEditUserBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.util.Logger

class EditUserFragment : Fragment() {

    private var filePath: String = ""
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
                    NewPetViewModel.INVALID_IMAGE_PATH_EMPTY -> Toast.makeText(context, "請上傳照片!", Toast.LENGTH_SHORT).show()
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

        binding.uploadUserIv.setOnClickListener {
            checkPermission()
        }

        return binding.root
    }

    private fun checkPermission() {
        val permission = ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //未取得權限，向使用者要求允許權限
            ActivityCompat.requestPermissions(
                this.requireActivity(), arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_EXTERNAL_STORAGE
            )
        }
        getLocalImg()
    }

    private fun getLocalImg() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //get image
                } else {
                    Toast.makeText(this.context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this.requireContext(), "resultCode = $resultCode , requestCode = $requestCode", Toast.LENGTH_SHORT).show()

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                PICK_IMAGE -> {
                    filePath = ImagePicker.getFilePath(data) ?: ""
                    if (filePath.isNotEmpty()) {
                        val imgPath = filePath
                        Logger.d(" = $imgPath")
                        Toast.makeText(this.requireContext(), imgPath, Toast.LENGTH_SHORT).show()
                        Glide.with(this.requireContext()).load(filePath).into(binding.uploadUserIv)

                        viewModel.uploadImage(imgPath)

                    } else {
                        Toast.makeText(this.requireContext(), "Upload failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 200
        private const val PICK_IMAGE = 2404
    }
}