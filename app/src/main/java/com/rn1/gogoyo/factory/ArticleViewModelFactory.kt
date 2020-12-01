package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.home.content.ArticleContentViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository

@Suppress("UNCHECKED_CAST")
class ArticleViewModelFactory(
    private val gogoyoRepository: GogoyoRepository,
    private val articles: Articles
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ArticleContentViewModel::class.java)) {
            return ArticleContentViewModel(gogoyoRepository, articles) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}