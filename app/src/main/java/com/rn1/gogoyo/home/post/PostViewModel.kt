package com.rn1.gogoyo.home.post

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: GogoyoRepository,
    private val argument: Walk
): ViewModel() {

    private val DEFAULT_IMAGE = "https://firebasestorage.googleapis.com/v0/b/turing-opus-296809.appspot.com/o/images%2Fdog_1.png?alt=media&token=0d165c4e-a3fe-47e3-b82e-a4c11d167419"

    private val _walk = MutableLiveData<Walk>().apply {
        value = argument
    }

    val walk: LiveData<Walk>
        get() = _walk

    var filePath: String = ""

    val post = MutableLiveData<Boolean>()
    /**
     *  do select pet logic
     */
    val selectedPetPositionList = MutableLiveData<MutableList<Int>>()
    val selectedPetIdList = MutableLiveData<MutableList<String>>()

    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    val title = MutableLiveData<String>()
    val content = MutableLiveData<String>()

    // Handle the error for post article
    private val _invalidInfo = MutableLiveData<Int>()

    val invalidInfo: LiveData<Int>
        get() = _invalidInfo

    private val _canPost = MutableLiveData<Boolean>()

    val canPost: LiveData<Boolean>
        get() = _canPost

    private val _userPetList = MutableLiveData<List<Pets>>()

    val userPetList: LiveData<List<Pets>>
        get() = _userPetList

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

        // check if post article from walk end
        if (argument.id != "") {
            title.value = "今天散步好開心^^"
            content.value = "這次散步時間 ${argument.period} 秒，走了 ${argument.distance ?: "0.12"} 公里， 下次還要挑戰走更久更遠!!"
            getWalkPets()

            val positionList = mutableListOf<Int>()
            val idList = mutableListOf<String>()

            for ((index, petId) in argument.petsIdList.withIndex()) {
                positionList.add(index)
                idList.add(petId)
            }

            selectedPetPositionList.value = positionList
            selectedPetIdList.value = idList

        } else {
            getUserPets()
            selectedPetPositionList.value = mutableListOf()
            selectedPetIdList.value = mutableListOf()
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun selectPet(id: String, position: Int) {
        Logger.w("id=$id, position=$position")

        val positionList = selectedPetPositionList.value
        val idList = selectedPetIdList.value

        Logger.w("positionList=$positionList, idList=$idList")

            if (positionList!!.contains(position)) {
                positionList.remove(position)
            } else {
                positionList.add(position)
            }
            selectedPetPositionList.value = positionList

            if (idList!!.contains(id)) {
                idList.remove(id)
            } else {
                idList.add(id)
            }
            selectedPetIdList.value = idList

    }


    private fun getUserPets(){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = repository.getAllPetsByUserId(UserManager.userUID!!)

            _userPetList.value = when (result) {
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

    fun uploadImage(path: String){
        coroutineScope.launch {

            filePath = when (val result = repository.getImageUri(path)) {
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

    fun getWalkPets(){
        coroutineScope.launch {

            _userPetList.value = when (val result = repository.getPetsByIdList(argument.petsIdList)) {
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

    fun checkArticleContent(){
        when {
            title.value.isNullOrEmpty() -> _invalidInfo.value = INVALID_FORMAT_TITLE_EMPTY
            content.value.isNullOrEmpty() -> _invalidInfo.value = INVALID_FORMAT_CONTENT_EMPTY
        }

        _canPost.value = !title.value.isNullOrEmpty() && !content.value.isNullOrEmpty()
    }

    fun post(){

        coroutineScope.launch {

            val article = Articles()
            article.title = title.value!!
            article.content = content.value
            article.authorId = UserManager.userUID
            article.petIdList = selectedPetIdList.value!!

            if (filePath == "") {
                filePath = DEFAULT_IMAGE
            }

            val images = mutableListOf<String>()
            images.add(filePath)

            article.images = images


            when(val result = repository.postArticle(article)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    _navigateToHome.value = true
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

    val decoration = object : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // add margin for recyclerView cell
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = GogoyoApplication.instance.resources.getDimensionPixelSize(R.dimen.cell_margin_8dp)
            }
        }
    }

    companion object {

        const val INVALID_FORMAT_TITLE_EMPTY = 0x11
        const val INVALID_FORMAT_CONTENT_EMPTY = 0x12
    }
}