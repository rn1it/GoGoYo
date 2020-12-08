package com.rn1.gogoyo.mypets.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileUserViewModel(val repository: GogoyoRepository): ViewModel() {

    val name = MutableLiveData<String>().apply {
        value = UserManager.userName
    }

    private val _userArticles = MutableLiveData<List<Articles>>()

    val userArticles: LiveData<List<Articles>>
        get() = _userArticles

    private val _userFavArticles = MutableLiveData<List<Articles>>()

    val userFavArticles: LiveData<List<Articles>>
        get() = _userFavArticles

    private val _onEdit = MutableLiveData<Boolean>()

    val onEdit: LiveData<Boolean>
        get() = _onEdit

    private val _onSureEdit = MutableLiveData<Boolean>()

    val onSureEdit: LiveData<Boolean>
        get() = _onSureEdit

    private val _onCancelEdit = MutableLiveData<Boolean>()

    val onCancelEdit: LiveData<Boolean>
        get() = _onCancelEdit

    private val _navigateToContent = MutableLiveData<Articles>()

    val navigateToContent: LiveData<Articles>
        get() = _navigateToContent


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
        getUserArticle()
        getUserFavArticle()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private fun getUserArticle() {

        coroutineScope.launch {

            _userArticles.value =
                when (val result = repository.getArticlesById(UserManager.userUID!!)) {
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
                when (val result = repository.getFavoriteArticlesById(UserManager.userUID!!)) {
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

    fun edit(){
        _onEdit.value = true
    }

    fun onDoneEdit(){
        _onEdit.value = null
    }

    fun onSureEdit(){
        _onSureEdit.value = true
    }

    fun onDoneSureEdit(){
        _onSureEdit.value = null
    }

    fun onCancelEdit(){
        _onCancelEdit.value = true
    }

    fun onDoneCancelEdit(){
        _onCancelEdit.value = null
    }

    fun navigateToContent(articles: Articles){
        _navigateToContent.value = articles
    }

    fun onDoneNavigateToContent(){
        _navigateToContent.value = null
    }
}