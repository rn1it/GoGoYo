package com.rn1.gogoyo.friend.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.source.GogoyoRepository

class FriendChatViewModel(val repository: GogoyoRepository): ViewModel() {

    private val _navigateToChatRoom = MutableLiveData<Boolean>()

    val navigateToChatRoom: LiveData<Boolean>
        get() = _navigateToChatRoom


    fun navigateToChatRoom(){
        _navigateToChatRoom.value = true
    }

    fun onDoneNavigateToChatRoom(){
        _navigateToChatRoom.value = null
    }

}