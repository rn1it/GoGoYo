package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.home.content.ArticleContentViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.walk.end.WalkEndViewModel
import com.rn1.gogoyo.walk.start.WalkStartViewModel

@Suppress("UNCHECKED_CAST")
class WalkEndViewModelFactory(
    private val gogoyoRepository: GogoyoRepository,
    private val walk: Walk
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(WalkEndViewModel::class.java)) {
            return WalkEndViewModel(gogoyoRepository, walk) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}