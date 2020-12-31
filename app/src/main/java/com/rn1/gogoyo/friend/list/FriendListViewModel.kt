package com.rn1.gogoyo.friend.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
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
import java.util.*

class FriendListViewModel(
    val repository: GogoyoRepository,
    val userId: String
): ViewModel() {

    var liveFriend = MutableLiveData<List<Friends>>()

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
        getUserLiveFriend()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getUserLiveFriend(){
        liveFriend = repository.getUserLiveFriend(UserManager.userUID!!, null)
    }

    private fun getUserFriends(){

//        friendStatus.value = friendShip

//        val status = when (friendShip) {
//            "朋友" -> 2
//            "好友邀請" -> 1
//            "等待中" -> 0
//            else -> null
//        }

        coroutineScope.launch {

            when (val result = repository.getUserFriends(userId, null)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val friends = result.data
                    getFriendListById( friends)
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

    fun getFriendListById(friends: List<Friends>) {

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
                            for (friend in friends) {
                                if (user.id == friend.friendId) {
                                    user.status = friend.status
                                    list.add(user)
                                }
                            }
                        }
                        // sort by user name
                        list.sortBy { it.name }
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

    fun onClickProfileBt(user: Users){
        coroutineScope.launch {
            val friend = Friends().apply {
                createdTime = Calendar.getInstance().timeInMillis
                friendId = user.id
                status = 2
            }
            when (val result = repository.setUserFriend(UserManager.userUID!!, friend)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    friend.apply {
                        friendId = UserManager.userUID!!
                        status = 2
                    }
                    updateFriendStatus(user, friend)
                    //  同步更新好友的好友狀態
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

    // 送出好友邀請後同步更新好友的好友狀態
    private fun updateFriendStatus(user: Users, friend: Friends) {
        coroutineScope.launch {
            when (val result = repository.setUserFriend(user.id, friend)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
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