package com.rn1.gogoyo.home.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository

class ArticleContentViewModel(
    private val repository: GogoyoRepository,
    private val arguments: Articles): ViewModel() {

    private val _article = MediatorLiveData<Articles>().apply {
        value = arguments
    }

    val article : LiveData<Articles>
    get() = _article

    private val _leaveArticle = MediatorLiveData<Boolean>()

    val leaveArticle : LiveData<Boolean>
        get() = _leaveArticle

    fun onLeaveArticle() {
        _leaveArticle.value = true
    }
}