package com.rn1.gogoyo.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository

class HomeViewModel(private val repository: GogoyoRepository): ViewModel() {

    private val _navigateToPost = MutableLiveData<Boolean>()

    val navigateToPost: LiveData<Boolean>
    get() = _navigateToPost

    private val _navigateToContent = MutableLiveData<Articles>()

    val navigateToContent: LiveData<Articles>
        get() = _navigateToContent


    fun navigateToContent(articles: Articles){
        _navigateToContent.value = articles
    }

    fun onDoneNavigateToContent(){
        _navigateToContent.value = null
    }

    fun onNavigateToPost(){
        _navigateToPost.value = true
    }

    fun onDoneNavigate(){
        _navigateToPost.value = null
    }
}