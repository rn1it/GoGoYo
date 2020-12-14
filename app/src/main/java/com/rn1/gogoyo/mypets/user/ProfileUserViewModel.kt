package com.rn1.gogoyo.mypets.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Friends
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class ProfileUserViewModel(
    val repository: GogoyoRepository,
    val userId: String
    ): ViewModel() {

    val outlineProvider =  MapOutlineProvider()

    // check profile is login user or not
    val isLoginUser = userId == UserManager.userUID

    var loginUser = MutableLiveData<Users>()

    var loginUserFriends = MutableLiveData<List<Friends>>()

    private val _user = MutableLiveData<Users>()

    val user: LiveData<Users>
        get() = _user

    private val _friendStatus = MutableLiveData<Int>()

    val friendStatus: LiveData<Int>
        get() = _friendStatus

    val profileBtText = MutableLiveData<String>()

    private val _userArticles = MutableLiveData<List<Articles>>()

    val userArticles: LiveData<List<Articles>>
        get() = _userArticles

    private val _userFavArticles = MutableLiveData<List<Articles>>()

    val userFavArticles: LiveData<List<Articles>>
        get() = _userFavArticles

    private val _navigateToContent = MutableLiveData<Articles>()

    val navigateToContent: LiveData<Articles>
        get() = _navigateToContent

    private val _navigateToEdit = MutableLiveData<String>()

    val navigateToEdit: LiveData<String>
        get() = _navigateToEdit

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
        // real time data
        getLoginUser()
        if (!isLoginUser) {
            getLoginUserFriendStatus()
            getFriendStatus()
        } else {
            profileBtText.value = "修改資料"
        }
        getUser()
        getUserArticle()
        getUserFavArticle()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getLoginUser(){
        loginUser = repository.getLiveUserById(UserManager.userUID!!)
    }

    private fun getLoginUserFriendStatus(){
        loginUserFriends = repository.getLiveUserFriendStatusById(UserManager.userUID!!)
    }

    private fun getUser() {
        coroutineScope.launch {

            _user.value =
                when (val result = repository.getUserById(userId)) {
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
                        _error.value =
                            GogoyoApplication.instance.getString(R.string.something_wrong)
                        _status.value = LoadStatus.ERROR
                        null
                    }
                }
        }
    }

    private fun getUserArticle() {

        coroutineScope.launch {

            _userArticles.value =
                when (val result = repository.getArticlesById(userId)) {
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
                        _error.value =
                            GogoyoApplication.instance.getString(R.string.something_wrong)
                        _status.value = LoadStatus.ERROR
                        null
                    }
                }
        }
    }

    private fun getUserFavArticle() {

        coroutineScope.launch {

            _userFavArticles.value =
                when (val result = repository.getFavoriteArticlesById(userId)) {
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
                        _error.value =
                            GogoyoApplication.instance.getString(R.string.something_wrong)
                        _status.value = LoadStatus.ERROR
                        null
                    }
                }
        }
    }

    fun navigateToContent(articles: Articles){
        _navigateToContent.value = articles
    }

    fun onDoneNavigateToContent(){
        _navigateToContent.value = null
    }

    fun onClickProfileBt(){
        if (isLoginUser) {
            _navigateToEdit.value = userId
        } else {

            when (_friendStatus.value) {
                -1 -> { // send friend invite
                    coroutineScope.launch {

                        val friend = Friends().apply {
                            createdTime = Calendar.getInstance().timeInMillis
                            friendId = userId
                            status = 0
                        }
                        when (val result = repository.setUserFriend(UserManager.userUID!!, friend)) {
                            is Result.Success -> {
                                _error.value = null
                                _status.value = LoadStatus.DONE
                                friend.apply {
                                    friendId = UserManager.userUID!!
                                    status = 1
                                }
                                updateFriendStatus(friend)
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
                1 -> {
                    coroutineScope.launch {
                        val friend = Friends().apply {
                            createdTime = Calendar.getInstance().timeInMillis
                            friendId = userId
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
                                updateFriendStatus(friend)
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
            }
        }
    }

    // 送出好友邀請後同步更新好友的好友狀態
    private fun updateFriendStatus(friend: Friends) {
        coroutineScope.launch {
            when (val result = repository.setUserFriend(userId, friend)) {
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

    fun getFriendStatus(){

        if (!isLoginUser) {
            coroutineScope.launch {

                when (val result = repository.getUserFriends(UserManager.userUID!!, null)){

                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE

                        val friends = result.data.filter { it.friendId == userId }
                        if (friends.isEmpty()) {
                            _friendStatus.value = -1
                            profileBtText.value = "送出好友邀請"
                        } else {
                            _friendStatus.value = friends[0].status

                            profileBtText.value = when (friends[0].status) {
                                0 -> "已發送邀請"
                                1 -> "接受好友邀請"
                                else -> "朋友"
                            }
                        }

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


    fun onDoneNavigateToEdit(){
        _navigateToEdit.value = null
    }

}