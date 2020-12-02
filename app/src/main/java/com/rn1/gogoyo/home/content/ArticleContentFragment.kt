package com.rn1.gogoyo.home.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentArticleContentBinding
import com.rn1.gogoyo.ext.getVmFactory

class ArticleContentFragment : Fragment() {

    private lateinit var binding: FragmentArticleContentBinding
    private val viewModel by viewModels<ArticleContentViewModel> { getVmFactory(ArticleContentFragmentArgs.fromBundle(requireArguments()).articleKey) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_content, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val viewPager = binding.articleImageViewPager
        val adapter = ArticleContentAdapter()
        viewPager.adapter = adapter


        viewModel.leaveArticle.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })




        return binding.root
    }
}