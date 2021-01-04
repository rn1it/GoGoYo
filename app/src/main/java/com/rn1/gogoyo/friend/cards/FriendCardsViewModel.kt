package com.rn1.gogoyo.friend.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Friends
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class FriendCardsViewModel(
    val repository: GogoyoRepository,
    val userId: String
    ): ViewModel(){

    private val now = Calendar.getInstance().timeInMillis
    val outlineProvider = MapOutlineProvider()

    private val _showBarkToast = MutableLiveData<String>()
    val showBarkToast: LiveData<String>
        get() = _showBarkToast

    private val _showVideoDialog = MutableLiveData<String>()
    val showVideoDialog: LiveData<String>
        get() = _showVideoDialog

    private val _user = MutableLiveData<Users>()
    val user: LiveData<Users>
        get() = _user

    private val _dataChange = MutableLiveData<List<Int>>()
    val dataChange: LiveData<List<Int>>
        get() = _dataChange

    private val _usersNotFriend = MutableLiveData<List<Users>>()
    val usersNotFriend: LiveData<List<Users>>
        get() = _usersNotFriend

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUser()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getAllUsers(){

        coroutineScope.launch {
            when (val result = repository.getAllUsers(userId)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val users = mutableListOf<Users>()
                    users.addAll(result.data)
                    val filterUsers = mutableListOf<Users>()
                    filterUsers.addAll(users.filter { it.petIdList.isNotEmpty() })
                    getUserFriends(filterUsers)
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

    private fun getUser() {
        coroutineScope.launch {

                when (val result = repository.getUserById(userId)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        _user.value = result.data
                        checkNewDate()
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

    private fun getUserFriends(allUsers: MutableList<Users>) {
        coroutineScope.launch {

            when (val result = repository.getUserFriends(userId, null)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val friends = result.data
                    getUsersNotFriend(allUsers, friends)
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

    private fun getUsersNotFriend(allUsers: MutableList<Users>, friends: List<Friends>) {

        val allUsersId = allUsers.map { it.id }
        val friendUsersId = friends.map { it.friendId }
        val notFriendsId = mutableListOf<String>().apply {
            addAll(allUsersId)
            removeAll(friendUsersId)
        }

        // random pick user
        notFriendsId.shuffle()

        val showList = mutableListOf<String>()

        if (notFriendsId.size >= 3) {
            showList.add(notFriendsId[0])
            showList.add(notFriendsId[1])
            showList.add(notFriendsId[2])
        } else {
            showList.addAll(notFriendsId)
        }

        val user = _user.value!!
        user.recommendList = showList
        user.enterFriendCardTime = now

        updateUsers(user)

        if (notFriendsId.isEmpty()){
            _usersNotFriend.value = mutableListOf()
        } else {
            getUserNotFriendInfo(showList)
        }
    }

    // detect pets change on user card
    fun dataChange(position: Int, itemListPosition: Int) {
        val list = mutableListOf<Int>()
        list.add(position)
        list.add(itemListPosition)
        _dataChange.value = list
    }

    fun setShowBarkToast(pet: Pets) {
        _showBarkToast.value = pet.voice
    }

    fun onDoneShowBarkToast() {
        _showBarkToast.value = null
    }

    fun setShowVideoDialog(pet: Pets) {
        _showVideoDialog.value = pet.video
    }

    fun onDoneShowVideoDialog() {
        _showVideoDialog.value = null
    }

    private fun checkNewDate(){

        val user = _user.value!!

        // for first time login user
        if (user.enterFriendCardTime == null) {
            getAllUsers()
        } else {

            // check entry time is today or not
            val diff = Date(now).date - Date(user.enterFriendCardTime!!).date

            // new day pick 3 cards
            if (diff != 0) {
                getAllUsers()
            } else {
                if (user.recommendList!!.isEmpty()) {
                    _usersNotFriend.value = mutableListOf()
                } else {
                    getUserNotFriendInfo(user.recommendList!!)
                }
                user.enterFriendCardTime = now
                updateUsers(user)
            }
        }
    }


    private fun updateUsers(user: Users){
        coroutineScope.launch {

            when (val result = repository.updateUser(user)) {

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

    private fun getUserNotFriendInfo(showList: List<String>){
        coroutineScope.launch {
            _usersNotFriend.value = when (val result = repository.getUsersById(showList)) {
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


    fun addOrPassCard(users: MutableList<Users>, cardFriendId: String, sendFriendInvite: Boolean) {

        _usersNotFriend.value = users

        val idList = users.map { it.id }
        val user = _user.value!!
        user.recommendList = idList
        updateUsers(user)

        coroutineScope.launch {

            if (sendFriendInvite) {
                val friend = Friends().apply {
                    createdTime = Calendar.getInstance().timeInMillis
                    friendId = cardFriendId
                    status = 0
                }
                when (val result = repository.setUserFriend(UserManager.userUID!!, friend)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        val id = friend.friendId
                        friend.apply {
                            friendId = UserManager.userUID!!
                            status = 1
                        }

                        updateFriendStatus(id, friend)
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

    // update another user's friend status at the same time
    private fun updateFriendStatus(id: String, friend: Friends) {
        coroutineScope.launch {
            when (val result = repository.setUserFriend(id, friend)) {
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
}