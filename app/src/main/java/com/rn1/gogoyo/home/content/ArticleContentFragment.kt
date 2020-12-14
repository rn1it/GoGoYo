package com.rn1.gogoyo.home.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentArticleContentBinding
import com.rn1.gogoyo.ext.getVmFactory


class ArticleContentFragment : Fragment() {

    private lateinit var binding: FragmentArticleContentBinding
    private val viewModel by viewModels<ArticleContentViewModel> { getVmFactory(
        ArticleContentFragmentArgs.fromBundle(
            requireArguments()
        ).articleKey
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_article_content,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val viewPager = binding.articleImageViewPager
        val adapter = ArticleContentAdapter()
        viewPager.adapter = adapter


        val petRecyclerView = binding.articleContentPetRv
        val petAdapter = ArticleContentPetImageAdapter()
        petRecyclerView.adapter = petAdapter



        val articleResponseRv = binding.articleRespRv
        val responseAdapter = ArticleResponseAdapter()
        articleResponseRv.adapter = responseAdapter
        articleResponseRv.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        viewModel.leaveArticle.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) findNavController().popBackStack()
            }
        })

        // observe every change in article
        viewModel.liveArticle.observe(viewLifecycleOwner, Observer {
            it?.let {
                responseAdapter.submitList(it.responseList)

                //TODO 發文到最底
//                binding.articleSv.post(Runnable { binding.articleSv.fullScroll(ScrollView.FOCUS_DOWN) })
            }
        })

        viewModel.petList.observe(viewLifecycleOwner, Observer {
            it?.let {
                petAdapter.submitList(it)
            }
        })

        viewModel.navigateToProfile.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalMyPetsFragment(it))
                viewModel.toProfileDone()
            }
        })

        return binding.root
    }
}