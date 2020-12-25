package com.rn1.gogoyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.CurrentFragmentType
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val repository: GogoyoRepository): ViewModel() {

    val currentFragmentType =  MutableLiveData<CurrentFragmentType>()

    private val _navigateToHomeByBottomNav = MutableLiveData<Boolean>()
    val navigateToHomeByBottomNav: LiveData<Boolean>
        get() = _navigateToHomeByBottomNav

    private val _navigateToStatisticByBottomNav = MutableLiveData<Boolean>()
    val navigateToStatisticByBottomNav: LiveData<Boolean>
        get() = _navigateToStatisticByBottomNav

    private val _navigateToWalk = MutableLiveData<Boolean>()
    val navigateToWalk: LiveData<Boolean>
        get() = _navigateToWalk

    private val _popBack = MutableLiveData<Boolean>()
    val popBack: LiveData<Boolean>
        get() = _popBack

    private val _toPostArticle = MutableLiveData<Boolean>()
    val toPostArticle: LiveData<Boolean>
        get() = _toPostArticle

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun loginAndSetUser(userUID: String, userName: String) {
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            when (val result = repository.login(userUID, userName)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE

                    val user = result.data
                    Logger.d("aaaaaaaa${result.data}")
                    if (user.image.isNullOrEmpty()) {
                        user.image = UserManager.userPhoto
                        updateUser(user)
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

    private fun updateUser(user: Users) {
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

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

    fun postArticle(){
        _toPostArticle.value = true
    }

    fun postArticleDone(){
        _toPostArticle.value = null
    }

    fun onNavigateToWalk(){
        _navigateToWalk.value = true
    }

    fun onDoneNavigateToWalk(){
        _navigateToWalk.value = null
    }

    fun back(){
        _popBack.value = true
    }

    fun navigateToHomeByBottomNav() {
        _navigateToHomeByBottomNav.value = true
    }

    fun onNavigateToHomeDone() {
        _navigateToStatisticByBottomNav.value = null
    }

    fun navigateToStatisticByBottomNav() {
        _navigateToStatisticByBottomNav.value = true
    }

    fun onNavigateToStatisticDone() {
        _navigateToStatisticByBottomNav.value = null
    }
}
