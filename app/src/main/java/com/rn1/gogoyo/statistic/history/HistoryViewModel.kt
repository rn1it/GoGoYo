package com.rn1.gogoyo.statistic.history

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Walk

class HistoryViewModel(val repository: GogoyoRepository): ViewModel() {

    private val userId = UserManager.userUID!!

    private val _walks = MutableLiveData<List<Walk>>()
    val walks: LiveData<List<Walk>>
        get() = _walks

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
        getAllWalks()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getAllWalks(){

        coroutineScope.launch {

            when(val result = repository.getWalkListByUserId(userId)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE

                    val walks = result.data
                    if (walks.isNotEmpty()) {
                        getWalksWithPetInfo(walks)
                    } else {
                        _walks.value = mutableListOf()
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

    private fun getWalksWithPetInfo(walks: List<Walk>) {

        coroutineScope.launch {

            when(val result = repository.getWalkListInfoByWalkList(walks)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data.sortedByDescending { it.createdTime }
                    _walks.value = result.data
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
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            // add margin for recyclerView cell
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = 0
            } else {
                outRect.top = GogoyoApplication.instance.resources.getDimensionPixelSize(R.dimen.cell_margin_8dp)
            }
        }
    }
}