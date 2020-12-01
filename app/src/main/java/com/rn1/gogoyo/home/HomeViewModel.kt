package com.rn1.gogoyo.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository

class HomeViewModel(private val repository: GogoyoRepository): ViewModel() {

    private val _navigateToPost = MutableLiveData<Boolean>()

    val navigateToPost: LiveData<Boolean>
    get() = _navigateToPost

    fun onNavigateToPost(){
        _navigateToPost.value = true
    }

    fun onDoneNavigate(){
        _navigateToPost.value = null
    }
}