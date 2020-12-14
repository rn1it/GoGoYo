package com.rn1.gogoyo.friend.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.model.Chatroom
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.rn1.gogoyo.model.Result

class FriendChatViewModel(
    val repository: GogoyoRepository,
    val userId: String
): ViewModel() {

    private val _navigateToChatRoom = MutableLiveData<Chatroom>()

    val navigateToChatRoom: LiveData<Chatroom>
        get() = _navigateToChatRoom

    var liveChatRoomList = MutableLiveData<List<Chatroom>>()

    private val _chatList = MutableLiveData<List<Chatroom>>()

    val chatList: LiveData<List<Chatroom>>
        get() = _chatList

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
        getUserChatRooms()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateToChatRoom(chatroom: Chatroom){
        _navigateToChatRoom.value = chatroom
    }

    fun onDoneNavigateToChatRoom(){
        _navigateToChatRoom.value = null
    }

    private fun getUserChatRooms(){
        liveChatRoomList = repository.getUserChatList(userId)

    }

    fun getChatRoomWithFriendInfo(list: List<Chatroom>) {

        coroutineScope.launch {

            _chatList.value = when (val result = repository.getChatRoomListWithUserInfo(list)) {
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

}