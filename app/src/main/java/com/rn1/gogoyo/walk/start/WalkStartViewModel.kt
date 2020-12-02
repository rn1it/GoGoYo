package com.rn1.gogoyo.walk.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository

class WalkStartViewModel(val repository: GogoyoRepository): ViewModel() {

    private val _navigateToEndWalk = MutableLiveData<Boolean>()

    val navigateToEndWalk: LiveData<Boolean>
        get() = _navigateToEndWalk


    fun onNavigateToEndWalk(){
        _navigateToEndWalk.value = true
    }

    fun onDoneNavigateToEndWalk(){
        _navigateToEndWalk.value = null
    }







}