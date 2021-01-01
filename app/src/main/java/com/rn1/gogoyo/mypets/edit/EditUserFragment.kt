package com.rn1.gogoyo.mypets.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.databinding.FragmentEditUserBinding
import com.rn1.gogoyo.ext.checkPermission
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.util.INVALID_FORMAT_INTRODUCTION_EMPTY
import com.rn1.gogoyo.util.INVALID_FORMAT_NAME_EMPTY
import com.rn1.gogoyo.util.INVALID_IMAGE_PATH_EMPTY
import com.rn1.gogoyo.util.Logger

class EditUserFragment : Fragment() {

    private var filePath: String = ""
    private lateinit var binding: FragmentEditUserBinding
    private val viewModel by viewModels<EditUserViewModel> {
        getVmFactory(EditUserFragmentArgs.fromBundle(requireArguments()).userIdKey)
    }

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
                    INVALID_FORMAT_NAME_EMPTY -> Toast.makeText(context, getString(R.string.empty_user_name), Toast.LENGTH_SHORT).show()
                    INVALID_FORMAT_INTRODUCTION_EMPTY -> Toast.makeText(context, getString(R.string.empty_introduction_text), Toast.LENGTH_SHORT).show()
                    INVALID_IMAGE_PATH_EMPTY -> Toast.makeText(context, getString(R.string.empty_user_image_text), Toast.LENGTH_SHORT).show()
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

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                PICK_IMAGE -> {
                    filePath = ImagePicker.getFilePath(data) ?: ""
                    if (filePath.isNotEmpty()) {
                        val imgPath = filePath
                        Glide.with(this.requireContext()).load(filePath).into(binding.uploadUserIv)

                        viewModel.uploadImage(imgPath)

                    } else {
                        Logger.d("Camera Task Cancelled")
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