package com.rn1.gogoyo.home.post

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.rn1.gogoyo.MainViewModel
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentPostBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.home.post.PostViewModel.Companion.INVALID_FORMAT_CONTENT_EMPTY
import com.rn1.gogoyo.home.post.PostViewModel.Companion.INVALID_FORMAT_TITLE_EMPTY
import com.rn1.gogoyo.walk.start.WalkImgAdapter

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private val viewModel by viewModels<PostViewModel> { getVmFactory(PostFragmentArgs.fromBundle(requireArguments()).walkKey) }
    private var filePath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.petsImageRv
        val adapter = PostPetAdapter(viewModel)

        recyclerView.adapter = adapter

        val imaRecyclerView = binding.imgRecyclerView
        val walkImgAdapter = WalkImgAdapter()
        imaRecyclerView.adapter = walkImgAdapter


        // observer main view model post button
        mainViewModel.toPostArticle.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    viewModel.checkArticleContent()
                    mainViewModel.postArticleDone()
                }
            }
        })

        viewModel.post.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.checkArticleContent()
                mainViewModel.postArticleDone()
            }
        })

        viewModel.userPetList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.invalidInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    INVALID_FORMAT_TITLE_EMPTY -> Toast.makeText(context, "標題不可空白", Toast.LENGTH_SHORT).show()
                    INVALID_FORMAT_CONTENT_EMPTY -> Toast.makeText(context, "內容不可空白", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.canPost.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) viewModel.post()
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(context, "發佈成功!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(NavigationDirections.actionGlobalHomeFragment())
                }
            }
        })

        viewModel.images.observe(viewLifecycleOwner, Observer {
            it?.let {
                walkImgAdapter.submitList(it)
            }
        })

        binding.articleUploadIv.setOnClickListener {
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
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        when (resultCode) {

            Activity.RESULT_OK -> {
                filePath = ImagePicker.getFilePath(data) ?: ""
                if (filePath.isNotEmpty()) {
                    val imgPath = filePath
                    Toast.makeText(this.requireContext(), imgPath, Toast.LENGTH_SHORT).show()

                    viewModel.uploadImage(imgPath)

                } else {
                    Toast.makeText(this.requireContext(), "Upload failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            ImagePicker.RESULT_ERROR -> Toast.makeText(
                this.requireContext(),
                ImagePicker.getError(data),
                Toast.LENGTH_SHORT
            ).show()
            else -> Toast.makeText(this.requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 200
    }
}