package com.rn1.gogoyo.friend.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Chatroom
import com.rn1.gogoyo.model.Friends
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users

class FriendListViewModel(
    val repository: GogoyoRepository,
    val userId: String
): ViewModel() {

    private val _friendList = MutableLiveData<List<Users>>()

    val friendList: LiveData<List<Users>>
        get() = _friendList

    val friendStatus = MutableLiveData<String>()

    private val _navigateToChatRoom = MutableLiveData<Chatroom>()

    val navigateToChatRoom: LiveData<Chatroom>
        get() = _navigateToChatRoom

    private val _navigateToProfile = MediatorLiveData<String>()
    val navigateToProfile: LiveData<String>
        get() = _navigateToProfile

    private val _status = MutableLiveData<LoadStatus>()

    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val outlineProvider = MapOutlineProvider()

    init {
//        getUserFriends("朋友")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getUserFriends(friendShip: String){

        friendStatus.value = friendShip

        val status = when (friendShip) {
            "朋友" -> 2
            "好友邀請" -> 1
            "等待中" -> 0
            else -> 2
        }

        coroutineScope.launch {

            when (val result = repository.getUserFriends(userId, status)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val friends = result.data
                    getFriendListById(status, friends)
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                }
                else -> {
                    _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                    _status.value = LoadStatus.ERROR
                }
            }
        }
    }

    private fun getFriendListById(status: Int, friends: List<Friends>) {

        val idList = mutableListOf<String>()

        for (friend in friends) {
            idList.add(friend.friendId)
        }

        if (idList.isEmpty()) {
            _friendList.value = null
        } else {
            coroutineScope.launch {

                when (val result = repository.getUsersById(idList)) {

                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        val list = mutableListOf<Users>()
                        for (user in result.data){
                            user.status = status
                            list.add(user)
                        }
                        _friendList.value = list
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadStatus.ERROR
                    }
                    else -> {
                        _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                        _status.value = LoadStatus.ERROR
                    }
                }
            }
        }
    }

    fun toChatRoom(friend: Users){

        coroutineScope.launch {

            _navigateToChatRoom.value = when (val result = repository.getChatRoom(userId, friend.id)) {

                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else -> {
                    _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }
        }

    }

    fun toProfile(id: String) {
        _navigateToProfile.value = id
    }

    fun toChatRoomDone(){
        _navigateToChatRoom.value = null
    }

    fun toProfileDone(){
        _navigateToProfile.value = null
    }
}