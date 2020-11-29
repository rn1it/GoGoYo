package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.MainViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.MyPetsViewModel
import com.rn1.gogoyo.walk.WalkViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: GogoyoRepository
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(WalkViewModel::class.java) ->
                    WalkViewModel(repository)

                isAssignableFrom(MyPetsViewModel::class.java) ->
                    MyPetsViewModel(repository)
                
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}