package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.MainViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: GogoyoRepository
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}