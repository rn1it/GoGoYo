package com.rn1.gogoyo.mypets.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository

class ProfileUserViewModel(val repository: GogoyoRepository): ViewModel() {

    val name = MutableLiveData<String>().apply {
        value = UserManager.userName
    }


    private val _onEdit = MutableLiveData<Boolean>()

    val onEdit: LiveData<Boolean>
        get() = _onEdit

    private val _onSureEdit = MutableLiveData<Boolean>()

    val onSureEdit: LiveData<Boolean>
        get() = _onSureEdit

    private val _onCancelEdit = MutableLiveData<Boolean>()

    val onCancelEdit: LiveData<Boolean>
        get() = _onCancelEdit

    private val _navigateToContent = MutableLiveData<Articles>()

    val navigateToContent: LiveData<Articles>
        get() = _navigateToContent

    fun edit(){
        _onEdit.value = true
    }

    fun onDoneEdit(){
        _onEdit.value = null
    }

    fun onSureEdit(){
        _onSureEdit.value = true
    }

    fun onDoneSureEdit(){
        _onSureEdit.value = null
    }

    fun onCancelEdit(){
        _onCancelEdit.value = true
    }

    fun onDoneCancelEdit(){
        _onCancelEdit.value = null
    }

    fun navigateToContent(articles: Articles){
        _navigateToContent.value = articles
    }

    fun onDoneNavigateToContent(){
        _navigateToContent.value = null
    }
}