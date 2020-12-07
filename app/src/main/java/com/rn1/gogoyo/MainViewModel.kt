package com.rn1.gogoyo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.CurrentFragmentType
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val repository: GogoyoRepository): ViewModel() {

    val currentFragmentType =  MutableLiveData<CurrentFragmentType>()

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

}