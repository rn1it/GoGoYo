package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.home.content.ArticleContentViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.edit.EditPetViewModel
import com.rn1.gogoyo.mypets.edit.EditUserViewModel
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.mypets.pet.ProfilePetViewModel
import com.rn1.gogoyo.mypets.user.ProfileUserViewModel
import com.rn1.gogoyo.walk.start.WalkStartViewModel

@Suppress("UNCHECKED_CAST")
class IdStringViewModelFactory(
    private val gogoyoRepository: GogoyoRepository,
    private val id:String
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(ProfilePetViewModel::class.java)) {
            ProfilePetViewModel(gogoyoRepository, id) as T

        } else if (modelClass.isAssignableFrom(ProfileUserViewModel::class.java)) {
            ProfileUserViewModel(gogoyoRepository, id) as T

        } else if (modelClass.isAssignableFrom(EditPetViewModel::class.java)) {
            EditPetViewModel(gogoyoRepository, id) as T

        } else if (modelClass.isAssignableFrom(EditUserViewModel::class.java)) {
            EditUserViewModel(gogoyoRepository, id) as T

        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}