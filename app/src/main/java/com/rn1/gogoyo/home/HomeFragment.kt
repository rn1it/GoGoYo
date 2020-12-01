package com.rn1.gogoyo.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentHomeBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Articles

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.articleRv
        val adapter = HomeAdapter(HomeAdapter.OnClickListener {
            viewModel.navigateToContent(it)
        })


        val a1 = Articles("001", "001001001001001001001001001001001001001", authorId = "hi0103")
        val a2 = Articles("002", "002", authorId = "hi0103")
        val a3 = Articles("0031", "0031", authorId = "hi0103")
        val a4 = Articles("0041", "004100410041004100410041004100410041004100410041004100410041", authorId = "hi0103")

        val list = mutableListOf<Articles>()
        list.add(a1)
        list.add(a2)
        list.add(a3)
        list.add(a4)


        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)


        adapter.submitList(list)

        viewModel.navigateToPost.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalPostFragment())
                viewModel.onDoneNavigate()
            }
        })

        viewModel.navigateToContent.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalArticleContentFragment(it))
                viewModel.onDoneNavigateToContent()
            }
        })


        return binding.root
    }
}