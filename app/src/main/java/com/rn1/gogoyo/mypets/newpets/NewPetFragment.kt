package com.rn1.gogoyo.mypets.newpets

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
import com.google.firebase.storage.StorageReference
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.databinding.FragmentNewPetBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_INTRODUCTION_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_NAME_EMPTY
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel.Companion.INVALID_FORMAT_SEX_EMPTY


class NewPetFragment : Fragment() {

    private lateinit var binding: FragmentNewPetBinding
    private val viewModel by viewModels<NewPetViewModel> { getVmFactory() }

    private var mStorageRef: StorageReference? = null
    private var imgPath: String = ""
    private var riversRef: StorageReference? = null

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
                    findNavController().navigate(NavigationDirections.actionGlobalMyPetsFragment(UserManager.userUID!!))
                }
                viewModel.onDoneNavigateToPet()
            }
        })

        binding.uploadPetIv.setOnClickListener{
            checkPermission()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PHOTO_FROM_GALLERY)
        }




        return binding.root
    }

    fun checkPermission(){
        val permissionList =
            mutableListOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )

        val requestList = mutableListOf<String>()
        for (permissionString in permissionList) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permissionString) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(permissionString)
            }
        }

        if (requestList.size > 0 ) {
            ActivityCompat.requestPermissions(requireActivity(), requestList.toTypedArray(), 0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PHOTO_FROM_GALLERY -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {

                        data?.let {

                            val uri = data.data
                            binding.uploadPetIv.setImageURI(uri)

                        }
                    }
                    Activity.RESULT_CANCELED -> { }
                }
            }
        }

    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode ==)
//
//
//    }










//    private fun initData() {
//        mStorageRef = FirebaseStorage.getInstance().reference
//    }

//    private fun checkPermission() {
//
//        val permission = ActivityCompat.checkSelfPermission(requireContext(),
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            //未取得權限，向使用者要求允許權限
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                REQUEST_EXTERNAL_STORAGE
//            )
//        } else {
//            getLocalImg()
//        }
//    }
//
//    private fun initView() {
//        binding.uploadPetIv.setOnClickListener { checkPermission() }
//        binding.uploadButton.setOnClickListener {
//            if (imgPath.isNotEmpty()) {
//                uploadImg(imgPath)
//            } else {
//            }
//        }
//    }
//
//    private fun uploadImg(path: String) {
//        val file = Uri.fromFile(File(path))
//        val metadata = StorageMetadata.Builder()
//            .setContentDisposition("universe")
//            .setContentType("image/jpg")
//            .build()
//        riversRef = mStorageRef?.child(file.lastPathSegment ?: "")
//        val uploadTask = riversRef?.putFile(file, metadata)
//        uploadTask?.addOnFailureListener { exception ->
//        }?.addOnSuccessListener {
//        }?.addOnProgressListener { taskSnapshot ->
//            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
//            if (progress >= 100) {
//            }
//        }
//    }


    companion object {
        private const val PHOTO_FROM_GALLERY = 4
        private const val REQUEST_EXTERNAL_STORAGE = 200
    }
}