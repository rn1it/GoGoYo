package com.rn1.gogoyo.home.content

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.ArticleResponse
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArticleContentViewModel(
    private val repository: GogoyoRepository,
    private val arguments: Articles): ViewModel() {

    private val _article = MutableLiveData<Articles>().apply {
        value = arguments
    }

    val article : LiveData<Articles>
        get() = _article

    private val _petList = MutableLiveData<List<Pets>>()

    val petList: LiveData<List<Pets>>
        get() = _petList

    val hasPet: LiveData<Boolean> =  Transformations.map(petList){
        it.isNotEmpty()
    }

    var liveArticle = MutableLiveData<Articles>()

    private val _isCollected = MutableLiveData<Boolean>()

    val isCollected: LiveData<Boolean>
        get() = _isCollected


    val response = MutableLiveData<String>()


    private val _leaveArticle = MediatorLiveData<Boolean>()

    val leaveArticle : LiveData<Boolean>
        get() = _leaveArticle

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
        getPets()
        getLiveArticle()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getPets(){

        coroutineScope.launch {

            _petList.value = when (val result = repository.getPetsByIdList(arguments.petIdList)) {
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

    private fun getLiveArticle() {
        liveArticle = repository.getRealTimeArticle(arguments.id)
    }

    fun response(){
        coroutineScope.launch {
            val articleResponse = ArticleResponse()
            articleResponse.userId = UserManager.userUID!!
            articleResponse.content = response.value!!
                when (val result = repository.responseArticle(arguments.id, articleResponse)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("response article success")
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
//                    null
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
                outRect.top = 0
            } else {
                outRect.top = GogoyoApplication.instance.resources.getDimensionPixelSize(R.dimen.cell_margin_4dp)
            }
        }
    }


    fun collectArticle(){
        coroutineScope.launch {

            _isCollected.value = when (val result = repository.collectArticle(arguments.id, UserManager.userUID!!)) {

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

    fun onLeaveArticle() {
        _leaveArticle.value = true
    }
}