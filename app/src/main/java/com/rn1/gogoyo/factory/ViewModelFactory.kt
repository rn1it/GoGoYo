package com.rn1.gogoyo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rn1.gogoyo.MainViewModel
import com.rn1.gogoyo.friend.chat.FriendChatViewModel
import com.rn1.gogoyo.friend.chat.chatRoom.ChatRoomViewModel
import com.rn1.gogoyo.home.HomeViewModel
import com.rn1.gogoyo.home.post.PostViewModel
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.MyPetsViewModel
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.mypets.pet.ProfilePetViewModel
import com.rn1.gogoyo.mypets.user.ProfileUserViewModel
import com.rn1.gogoyo.walk.WalkViewModel
import com.rn1.gogoyo.walk.end.WalkEndViewModel
import com.rn1.gogoyo.walk.start.WalkStartViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: GogoyoRepository
): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                isAssignableFrom(PostViewModel::class.java) ->
                    PostViewModel(repository)

                isAssignableFrom(WalkViewModel::class.java) ->
                    WalkViewModel(repository)

                isAssignableFrom(MyPetsViewModel::class.java) ->
                    MyPetsViewModel(repository)

                isAssignableFrom(ChatRoomViewModel::class.java) ->
                    ChatRoomViewModel(repository)

                isAssignableFrom(FriendChatViewModel::class.java) ->
                    FriendChatViewModel(repository)

                isAssignableFrom(NewPetViewModel::class.java) ->
                    NewPetViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}