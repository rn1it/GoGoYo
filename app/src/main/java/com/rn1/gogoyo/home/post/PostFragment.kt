package com.rn1.gogoyo.home.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rn1.gogoyo.MainViewModel
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentPostBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.home.post.PostViewModel.Companion.INVALID_FORMAT_CONTENT_EMPTY
import com.rn1.gogoyo.home.post.PostViewModel.Companion.INVALID_FORMAT_TITLE_EMPTY

class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private val viewModel by viewModels<PostViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.petsImageRv
        val adapter = PostPetAdapter()

        recyclerView.adapter = adapter


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

        return binding.root
    }

}