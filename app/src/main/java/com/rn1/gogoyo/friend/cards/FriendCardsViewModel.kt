package com.rn1.gogoyo.friend.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Friends
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FriendCardsViewModel(
    val repository: GogoyoRepository,
    val userId: String
    ): ViewModel(){

    private val _dataChange = MutableLiveData<List<Int>>()

    val dataChange: LiveData<List<Int>>
        get() = _dataChange

    val outlineProvider = MapOutlineProvider()

    private val _usersNotFriend = MutableLiveData<List<Users>>()

    val usersNotFriend: LiveData<List<Users>>
        get() = _usersNotFriend

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadStatus>()

    val status: LiveData<LoadStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getAllUsers()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onClick(){
        Logger.i("aaaaaaaaaaaaaaaaa")
    }










    fun getAllUsers(){

        coroutineScope.launch {

            when (val result = repository.getAllUsers(userId)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val users = mutableListOf<Users>()
                    users.addAll(result.data)
                    getUserFriends(users)
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

        coroutineScope.launch {

            val allUsersId = allUsers.map { it.id }
            val friendUsersId = friends.map { it.friendId }
            val notFriendsId = mutableListOf<String>().apply {
                addAll(allUsersId)
                removeAll(friendUsersId)
            }
            Logger.d("allUsersId = $allUsersId , friendUsersId = $friendUsersId , notFriendsId = $notFriendsId")
            //test
//            notFriendsId.add("7pYCfBdX53Zdjrshf4JHu2jgKvu2")
            _usersNotFriend.value = when (val result = repository.getUsersById(notFriendsId)) {
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

    fun dataChange(position: Int, itemListPosition: Int) {
        //test
        Logger.d("dataChange = $position")
        val list = mutableListOf<Int>()
        list.add(position)
        list.add(itemListPosition)
        _dataChange.value = list
    }
}