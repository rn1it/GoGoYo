package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.MainViewModel
import com.rn1.gogoyo.home.HomeViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.MyPetsViewModel
import com.rn1.gogoyo.walk.WalkViewModel
import com.rn1.gogoyo.walk.end.WalkEndViewModel
import com.rn1.gogoyo.walk.start.WalkStartViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: GogoyoRepository
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                isAssignableFrom(WalkViewModel::class.java) ->
                    WalkViewModel(repository)

                isAssignableFrom(WalkStartViewModel::class.java) ->
                    WalkStartViewModel(repository)

                isAssignableFrom(WalkEndViewModel::class.java) ->
                    WalkEndViewModel(repository)

                isAssignableFrom(MyPetsViewModel::class.java) ->
                    MyPetsViewModel(repository)
                
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}