package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.home.content.ArticleContentViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.edit.EditPetViewModel
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.walk.start.WalkStartViewModel

@Suppress("UNCHECKED_CAST")
class EditPetViewModelFactory(
    private val gogoyoRepository: GogoyoRepository,
    private val petId:String
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(EditPetViewModel::class.java)) {
            return EditPetViewModel(gogoyoRepository, petId) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}