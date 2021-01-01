package com.rn1.gogoyo.mypets.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditPetViewModel(
    val repository: GogoyoRepository,
    private val argument: String
): ViewModel() {

    val outlineProvider =  MapOutlineProvider()

    private var imageFilePath: String? = null
    private var audioFilePath: String? = null
    private var videoFilePath: String? = null

    private val _showUploadingToast = MutableLiveData<Boolean>()
    val showUploadingToast: LiveData<Boolean>
        get() = _showUploadingToast

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private val _pet = MutableLiveData<Pets>()
    val pet: LiveData<Pets>
        get() = _pet

    val name = MutableLiveData<String>()
    val breed = MutableLiveData<String>()
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

    val canEditPet = MutableLiveData<Boolean>()

    private val _navigateToPets = MutableLiveData<Boolean>()
    val navigateToPets: LiveData<Boolean>
        get() = _navigateToPets

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getPetById()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getPetById(){
        coroutineScope.launch {

           when(val result = repository.getPetsById(argument)) {

                is Result.Success -> {

                    _error.value = null
                    _status.value = LoadStatus.DONE

                    // set pet old data
                    _pet.value = result.data
                    name.value = result.data.name
                    introduction.value = result.data.introduction
                    breed.value = result.data.breed

                    imageFilePath = result.data.image
                    audioFilePath = result.data.voice
                    videoFilePath = result.data.video

                    selectedSexRadio.value = when (result.data.sex) {
                        "男生" -> R.id.radioBoy
                        "女生" -> R.id.radioGirl
                        else -> 0
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

    fun checkPetInfo(){
        canEditPet.value = !name.value.isNullOrEmpty()
                && !introduction.value.isNullOrBlank()
                && petSex.isNotEmpty()
                && !imageFilePath.isNullOrBlank()
        when {
            imageFilePath.isNullOrBlank() -> _invalidInfo.value = INVALID_IMAGE_PATH_EMPTY
            name.value.isNullOrEmpty() -> _invalidInfo.value = INVALID_FORMAT_NAME_EMPTY
            petSex.isEmpty() -> _invalidInfo.value = INVALID_FORMAT_SEX_EMPTY
            introduction.value.isNullOrBlank() -> _invalidInfo.value = INVALID_FORMAT_INTRODUCTION_EMPTY
        }
    }

    fun editPet(){
        val pet = pet.value!!
        pet.name = name.value!!
        pet.introduction = introduction.value
        pet.sex = petSex
        pet.breed = breed.value
        pet.image = imageFilePath
        pet.voice = audioFilePath
        pet.video = videoFilePath


        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            when(val result = repository.editPets(pet)) {
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

    fun uploadAudio(uri: Uri){

        if (_showProgressBar.value == true) {
            _showUploadingToast.value = true
        } else {

            _showProgressBar.value = true

            coroutineScope.launch {

                when (val result = repository.getAudioUri(uri)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        Logger.d("uri = ${result.data}")
                        audioFilePath = result.data
                        uploadFinished()
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadStatus.ERROR
                        uploadFinished()
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadStatus.ERROR
                        uploadFinished()
                    }
                    else -> {
                        _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                        _status.value = LoadStatus.ERROR
                        uploadFinished()
                    }
                }
            }
        }
    }

    fun uploadVideo(uri: Uri){
        if (_showProgressBar.value == true) {
            _showUploadingToast.value = true
        } else {
            _showProgressBar.value = true

            coroutineScope.launch {

                when (val result = repository.getVideoUri(uri)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadStatus.DONE
                        Logger.d("uri = ${result.data}")
                        videoFilePath = result.data
                        uploadFinished()
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadStatus.ERROR
                        uploadFinished()
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadStatus.ERROR
                        uploadFinished()
                    }
                    else -> {
                        _error.value =
                            GogoyoApplication.instance.getString(R.string.something_wrong)
                        _status.value = LoadStatus.ERROR
                        uploadFinished()
                    }
                }
            }
        }
    }

    fun onDoneShowUploadingToast(){
        _showUploadingToast.value = null
    }

    private fun uploadFinished(){
        _showProgressBar.value = false
    }

    fun onDoneNavigateToPet(){
        _navigateToPets.value = null
    }

}