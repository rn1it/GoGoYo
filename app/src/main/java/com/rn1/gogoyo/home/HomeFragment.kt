package com.rn1.gogoyo.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

class HomeFragment : Fragment(){

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


        val a1 = Articles("001", "001001001001001001001001001001001001001")
        val a2 = Articles("002", "002")
        val a3 = Articles("0031", "0031")
        val a4 = Articles("0041", "004100410041004100410041004100410041004100410041004100410041")

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

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(query: String): Boolean {
                adapter.submitList(filter(list, query))
                return true
            }
        })

        return binding.root
    }

    // return query list
    fun filter(list: List<Articles>, query: String): List<Articles> {

        val lowerCaseQueryString = query.toLowerCase()
        val filteredList = mutableListOf<Articles>()

        for (article in list) {
            val author = article.id.toLowerCase()
            val content = article.content ?: "" .toLowerCase()

            if (author.contains(lowerCaseQueryString) || content.contains(lowerCaseQueryString)) {
                filteredList.add(article)
            }
        }

        return filteredList
    }

}