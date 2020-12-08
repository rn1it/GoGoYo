package com.rn1.gogoyo.walk

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WalkViewModel(val repository: GogoyoRepository): ViewModel() {

    val outlineProvider =  MapOutlineProvider()

    /**
     *  do select pet logic
     */
    val selectedPetPositionList = MutableLiveData<MutableList<Int>>()
    val selectedPetIdList = MutableLiveData<MutableList<String>>()

    private val _navigateToStartWalk = MutableLiveData<MutableList<String>>()

    val navigateToStartWalk: LiveData<MutableList<String>>
        get() = _navigateToStartWalk

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
        getUserPets()
        selectedPetPositionList.value = mutableListOf()
        selectedPetIdList.value = mutableListOf()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * select pet and get selectedPetIdList
     */
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

    fun onNavigateToStartWalk(){
        _navigateToStartWalk.value = selectedPetIdList.value
    }

    fun onDoneNavigateToStartWalk(){
        _navigateToStartWalk.value = null
    }

}