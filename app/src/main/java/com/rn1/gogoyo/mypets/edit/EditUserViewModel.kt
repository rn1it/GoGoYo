package com.rn1.gogoyo.mypets.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditUserViewModel(
    val repository: GogoyoRepository,
    private val argument: String
): ViewModel() {

    private var imageFilePath: String? = null

    val outlineProvider =  MapOutlineProvider()

    private val _user = MutableLiveData<Users>()

    val user: LiveData<Users>
        get() = _user

    val name = MutableLiveData<String>()

    val introduction = MutableLiveData<String>()

    private val _invalidInfo = MutableLiveData<Int>()

    val invalidInfo: LiveData<Int>
        get() = _invalidInfo

    val canEditUser = MutableLiveData<Boolean>()

    private val _navigateToProfileUser = MutableLiveData<Boolean>()

    val navigateToProfileUser: LiveData<Boolean>
        get() = _navigateToProfileUser


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

    private fun getUser() {
        coroutineScope.launch {

            when (val result = repository.getUserById(argument)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    name.value = result.data.name
                    introduction.value = result.data.introduction
                    _user.value = result.data
                    imageFilePath = result.data.image
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
                    _error.value =
                        GogoyoApplication.instance.getString(R.string.something_wrong)
                    _status.value = LoadStatus.ERROR
                }
            }
        }
    }

    fun checkUserInfo(){
        canEditUser.value = !name.value.isNullOrEmpty()
                && !introduction.value.isNullOrBlank()
                && !imageFilePath.isNullOrBlank()
        when {
            imageFilePath.isNullOrBlank() -> _invalidInfo.value = INVALID_IMAGE_PATH_EMPTY
            name.value.isNullOrEmpty() -> _invalidInfo.value = INVALID_FORMAT_NAME_EMPTY
            introduction.value.isNullOrBlank() -> _invalidInfo.value = INVALID_FORMAT_INTRODUCTION_EMPTY
        }
    }

    fun editUser(){
        val user = user.value!!
        user.name = name.value!!
        user.introduction = introduction.value!!
        user.image = imageFilePath

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            when(val result = repository.editUsers(user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    _navigateToProfileUser.value = true
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

    fun onDoneNavigateToUser(){
        _navigateToProfileUser.value = null
    }

    fun uploadImage(path: String){
        coroutineScope.launch {

            imageFilePath = when (val result = repository.getImageUri(path)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("uri = ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    ""
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    ""
                }
                else -> {
                    _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                    _status.value = LoadStatus.ERROR
                    ""
                }
            }
        }
    }
}