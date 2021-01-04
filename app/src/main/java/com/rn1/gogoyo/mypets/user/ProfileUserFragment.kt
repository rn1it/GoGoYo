package com.rn1.gogoyo.mypets.user

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
import com.google.android.material.tabs.TabLayoutMediator
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentProfileUserBinding
import com.rn1.gogoyo.ext.checkPermission
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.Logger
import com.rn1.gogoyo.util.REQUEST_EXTERNAL_STORAGE

class ProfileUserFragment(val userId: String) : Fragment() {

    private lateinit var binding: FragmentProfileUserBinding
    private val viewModel by viewModels<ProfileUserViewModel> {
        getVmFactory(userId)
    }
    private var filePath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_user, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val tabLayout = binding.profileArticleTabLayout

        val pagerAdapter = ProfileUserArticlePagerAdapter(viewModel)
        val viewPager = binding.profileArticleViewPager

        // disable outer fragment slide
        viewPager.isUserInputEnabled = false
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.user_article_text)
                1 -> tab.text = getString(R.string.collect_text)
            }
            viewPager.currentItem = tab.position
        }.attach()

        viewModel.loginUserFriends.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.getFriendStatus()
            }
        })

        viewModel.viewPagerList.observe(viewLifecycleOwner, Observer {
            it?.let {
                pagerAdapter.submitList(it)
            }
        })

        viewModel.navigateToContent.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalArticleContentFragment(it))
                viewModel.onDoneNavigateToContent()
            }
        })

        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalEditUserFragment(it))
                viewModel.onDoneNavigateToEdit()
            }
        })

        binding.userProfileIv.setOnClickListener{
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //get image
                } else {
                    Toast.makeText(this.context, "開啟權限後即可使用此功能", Toast.LENGTH_SHORT).show()
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
                    Glide.with(this.requireContext()).load(filePath).into(binding.userProfileIv)

                    viewModel.uploadImage(imgPath)

                } else {
                    Toast.makeText(this.requireContext(), "上傳失敗", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            ImagePicker.RESULT_ERROR -> Toast.makeText(
                this.requireContext(),
                ImagePicker.getError(data),
                Toast.LENGTH_SHORT
            ).show()
            else -> Logger.d("Camera Task Cancelled")
        }
    }
}