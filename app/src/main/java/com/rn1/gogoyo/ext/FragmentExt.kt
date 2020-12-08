package com.rn1.gogoyo.ext

import androidx.fragment.app.Fragment
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.factory.ArticleViewModelFactory
import com.rn1.gogoyo.factory.EditPetViewModelFactory
import com.rn1.gogoyo.factory.ViewModelFactory
import com.rn1.gogoyo.factory.WalkStartViewModelFactory
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

fun Fragment.getVmFactory(petId: String): EditPetViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return EditPetViewModelFactory(repository, petId)
}