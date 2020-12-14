package com.rn1.gogoyo.friend.cards

import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.Logger

class FriendCardsViewModel(val repository: GogoyoRepository, val userId: String): ViewModel(){

fun onClick(){
    Logger.i("aaaaaaaaaaaaaaaaa")
}

}