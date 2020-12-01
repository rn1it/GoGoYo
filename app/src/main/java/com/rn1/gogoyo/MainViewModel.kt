package com.rn1.gogoyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.CurrentFragmentType

class MainViewModel(private val repository: GogoyoRepository): ViewModel() {

    private val _navigateToWalk = MutableLiveData<Boolean>()

    val navigateToWalk: LiveData<Boolean>
        get() = _navigateToWalk

    val currentFragmentType =  MutableLiveData<CurrentFragmentType>()

    fun onNavigateToWalk(){
        _navigateToWalk.value = true
    }

    fun onDoneNavigateToWalk(){
        _navigateToWalk.value = null
    }
}