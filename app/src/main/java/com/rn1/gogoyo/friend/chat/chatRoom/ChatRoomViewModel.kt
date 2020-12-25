package com.rn1.gogoyo.friend.chat.chatRoom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Chatroom
import com.rn1.gogoyo.model.Messages
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.util.Logger
import java.util.*

class ChatRoomViewModel(
    val repository: GogoyoRepository,
    val chatRoom: Chatroom
): ViewModel() {

    var friendId  = ""

    private val _friend = MutableLiveData<Users>()

    val friend: LiveData<Users>
        get() = _friend

    val content = MutableLiveData<String>()

    private val _clearMsg = MutableLiveData<Boolean>()

    val clearMsg: LiveData<Boolean>
        get() = _clearMsg

    var liveMessages = MutableLiveData<List<Messages>>()

    private val _liveMessagesWithUserInfo = MutableLiveData<List<Messages>>()

    val liveMessagesWithUserInfo: LiveData<List<Messages>>
        get() = _liveMessagesWithUserInfo

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

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
        getLiveMessages()
        getAnotherOne()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getAnotherOne(){

        friendId  = chatRoom.userList.filter { it != UserManager.userUID }[0]

        coroutineScope.launch {

            _friend.value = when (val result = repository.getUserById(friendId)) {

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

    private fun getLiveMessages() {
        liveMessages = repository.getLiveChatRoomMessages(chatRoom.id)
        _status.value = LoadStatus.DONE
        _refreshStatus.value = false
    }

    fun getLiveMessagesWithUserInfo(list: List<Messages>){

        coroutineScope.launch {
            _liveMessagesWithUserInfo.value = when (val result = repository.getLiveChatRoomMessagesWithUserInfo(list)) {
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


    fun sendMessage(){
        coroutineScope.launch {
            val message = Messages(UserManager.userUID!!, friendId, content.value!!)
            Logger.d("message = $message")

            when (val result = repository.sendMessage(chatRoom.id, message)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    updateChatRoom()
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

    private fun updateChatRoom(){
        coroutineScope.launch {

            chatRoom.lastMsg = content.value!!
            chatRoom.msgTime = Calendar.getInstance().timeInMillis
            chatRoom.lastSenderId = UserManager.userUID

            _clearMsg.value = when (val result = repository.updateChatRoom(chatRoom)) {
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