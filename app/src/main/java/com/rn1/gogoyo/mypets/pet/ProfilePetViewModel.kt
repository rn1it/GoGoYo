package com.rn1.gogoyo.mypets.pet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfilePetViewModel(
    val repository: GogoyoRepository,
    val userId: String
    ): ViewModel() {

    // check profile is login user or not
    val isLoginUser = userId == UserManager.userUID

    private val _petList = MutableLiveData<List<Pets>>()

    val petList: LiveData<List<Pets>>
        get() = _petList

    val pet = MutableLiveData<Pets>()

    private val _onEdit = MutableLiveData<Boolean>()

    val onEdit: LiveData<Boolean>
        get() = _onEdit

    private val _onSureEdit = MutableLiveData<Boolean>()

    val onSureEdit: LiveData<Boolean>
        get() = _onSureEdit

    private val _onCancelEdit = MutableLiveData<Boolean>()

    val onCancelEdit: LiveData<Boolean>
        get() = _onCancelEdit

    private val _navigateToNewPet = MutableLiveData<Boolean>()

    val navigateToNewPet: LiveData<Boolean>
        get() = _navigateToNewPet

    private val _editPet = MutableLiveData<String>()

    val editPet: LiveData<String>
        get() = _editPet


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
        getMyPets()

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getMyPets(){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = repository.getAllPetsByUserId(userId)

            _petList.value = when (result) {
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

    fun onNavigateToNewPet(){
        _navigateToNewPet.value = true
    }

    fun onDoneNavigateToNewPet(){
        _navigateToNewPet.value = null
    }

    fun editPet(){
        _editPet.value = pet.value!!.id
    }

    fun toEditPetDone(){
        _editPet.value = null
    }
//    fun edit(){
//        _onEdit.value = true
//    }
//
//    fun onDoneEdit(){
//        _onEdit.value = null
//    }

//    fun onSureEdit(){
//        _onSureEdit.value = true
//    }
//
//    fun onDoneSureEdit(){
//        _onSureEdit.value = null
//    }

//    fun onCancelEdit(){
//        _onCancelEdit.value = true
//    }
//
//    fun onDoneCancelEdit(){
//        _onCancelEdit.value = null
//    }

}