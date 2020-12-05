package com.rn1.gogoyo.mypets.newpets

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

class NewPetViewModel(val repository: GogoyoRepository): ViewModel() {

    val name = MutableLiveData<String>()

    val introduction = MutableLiveData<String>()

    val selectedSexRadio = MutableLiveData<Int>()

    private val petSex: String
        get() = when (selectedSexRadio.value) {
            R.id.radioBoy -> "男生"
            R.id.radioGirl -> "女生"
            else -> ""
        }

    // Handle the error for adding new pet
    private val _invalidInfo = MutableLiveData<Int>()

    val invalidInfo: LiveData<Int>
        get() = _invalidInfo

    val canAddPet = MutableLiveData<Boolean>()

    private val _navigateToPets = MutableLiveData<Boolean>()

    val navigateToPets: LiveData<Boolean>
        get() = _navigateToPets


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

    fun checkPetInfo(){
        canAddPet.value = !name.value.isNullOrEmpty()
                && !introduction.value.isNullOrBlank()
                && petSex.isNotEmpty()
        when {
            name.value.isNullOrEmpty() -> _invalidInfo.value = INVALID_FORMAT_NAME_EMPTY
            petSex.isEmpty() -> _invalidInfo.value = INVALID_FORMAT_SEX_EMPTY
            introduction.value.isNullOrBlank() -> _invalidInfo.value = INVALID_FORMAT_INTRODUCTION_EMPTY
        }
    }

    fun addNewPet(){
        val pet = Pets()
        pet.name = name.value!!
        pet.introduction = introduction.value
        pet.sex = petSex

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            when(val result = repository.newPets(pet, UserManager.userUID!!)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    _navigateToPets.value = true
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



    fun onDoneNavigateToPet(){
        _navigateToPets.value = null
    }

    companion object {

        const val INVALID_FORMAT_NAME_EMPTY = 0x11
        const val INVALID_FORMAT_INTRODUCTION_EMPTY = 0x12
        const val INVALID_FORMAT_SEX_EMPTY          = 0x13
    }

}