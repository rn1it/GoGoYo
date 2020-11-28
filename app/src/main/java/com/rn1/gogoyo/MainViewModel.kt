package com.rn1.gogoyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository

class MainViewModel(private val repository: GogoyoRepository): ViewModel() {

    private val _navigateToWalk = MutableLiveData<Boolean>()

    val navigateToWalk: LiveData<Boolean>
        get() = _navigateToWalk

    fun onNavigateToWalk(){
        _navigateToWalk.value = true
    }

    fun onDoneNavigateTOWalk(){
        _navigateToWalk.value = null
    }
}