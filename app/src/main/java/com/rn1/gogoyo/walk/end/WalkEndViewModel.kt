package com.rn1.gogoyo.walk.end

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WalkEndViewModel(
    val repository: GogoyoRepository,
    private val arguments: Walk
    ): ViewModel() {

    private val _walk = MutableLiveData<Walk>().apply {
        value = arguments
    }
    val walk: LiveData<Walk>
        get() = _walk

    private val _petList = MutableLiveData<List<Pets>>()
    val petList: LiveData<List<Pets>>
        get() = _petList

    private val _navigateToPost = MutableLiveData<Walk>()
    val navigateToPost: LiveData<Walk>
        get() = _navigateToPost

    private val _navigateToStatistic = MutableLiveData<Boolean>()
    val navigateToStatistic: LiveData<Boolean>
        get() = _navigateToStatistic

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        if (arguments.petsIdList.isNotEmpty()) {
            getPets()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getPets(){

        coroutineScope.launch {

            _petList.value = when (val result = repository.getPetsByIdList(arguments.petsIdList!!)) {
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

    fun navigateToPost(){
        _navigateToPost.value = walk.value
    }

    fun navigateToPostDone(){
        _navigateToPost.value = null
    }

    fun navigateToStatistic(){
        _navigateToStatistic.value = true
    }

    fun navigateToStatisticDone(){
        _navigateToStatistic.value = null
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

}