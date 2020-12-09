package com.rn1.gogoyo.ext

import androidx.fragment.app.Fragment
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.factory.*
import com.rn1.gogoyo.model.Articles

fun Fragment.getVmFactory(): ViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(articles: Articles): ArticleViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return ArticleViewModelFactory(repository, articles)
}

fun Fragment.getVmFactory(petIdList: List<String>): WalkStartViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return WalkStartViewModelFactory(repository, petIdList)
}

fun Fragment.getVmFactory(id: String): IdStringViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return IdStringViewModelFactory(repository, id)
}


