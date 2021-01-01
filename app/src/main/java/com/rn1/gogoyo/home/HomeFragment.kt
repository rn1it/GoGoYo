package com.rn1.gogoyo.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.util.Logger
import java.util.*


class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Logger.d("onCreateView")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val recyclerView = binding.articleRv
        val adapter = HomeAdapter(HomeAdapter.OnClickListener {
            viewModel.navigateToContent(it)
        })
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = staggeredGridLayoutManager

        viewModel.articleList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)

                binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false

                    override fun onQueryTextChange(query: String): Boolean {
                        adapter.submitList(filter(it, query) )
                        return true
                    }
                })

            }
        })

        viewModel.navigateToPost.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalPostFragment(Walk()))
                viewModel.onDoneNavigate()
            }
        })

        viewModel.navigateToContent.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalArticleContentFragment(it))
                viewModel.onDoneNavigateToContent()
            }
        })

        viewModel.refreshStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.swipeRefreshLayout.isRefreshing = it
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        return binding.root
    }

    // return query list
    fun filter(list: List<Articles>, query: String): List<Articles> {

        val lowerCaseQueryString = query.toLowerCase(Locale.ROOT)
        val filteredList = mutableListOf<Articles>()

        for (article in list) {
            val author = article.author!!.name.toLowerCase(Locale.ROOT)
            val content = article.content ?: "" .toLowerCase(Locale.ROOT)

            if (author.contains(lowerCaseQueryString) || content.contains(lowerCaseQueryString)) {
                filteredList.add(article)
            }
        }

        return filteredList
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Logger.d("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("onCreate")
    }

    override fun onResume() {
        super.onResume()
        Logger.d("onResume")
    }

    override fun onStart() {
        super.onStart()
        Logger.d("onStart")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Logger.d("onActivityCreated")
    }

    override fun onStop() {
        super.onStop()
        Logger.d("onStop")
    }

    override fun onPause() {
        super.onPause()
        Logger.d("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Logger.d("onDetach")
    }

}