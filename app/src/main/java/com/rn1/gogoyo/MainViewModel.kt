package com.rn1.gogoyo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.CurrentFragmentType

class MainViewModel(private val repository: GogoyoRepository): ViewModel() {

    val currentFragmentType =  MutableLiveData<CurrentFragmentType>()

    private val _navigateToWalk = MutableLiveData<Boolean>()

    val navigateToWalk: LiveData<Boolean>
        get() = _navigateToWalk

    private val _popBack = MutableLiveData<Boolean>()

    val popBack: LiveData<Boolean>
        get() = _popBack

    fun onNavigateToWalk(){
        _navigateToWalk.value = true
    }

    fun onDoneNavigateToWalk(){
        _navigateToWalk.value = null
    }

    fun back(){
        _popBack.value = true
    }

}