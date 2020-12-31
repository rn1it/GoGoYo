package com.rn1.gogoyo.statistic.total

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TotalWalkViewModel(val repository: GogoyoRepository): ViewModel() {

    private val userId = UserManager.userUID!!

    var totalTime = 0L
    var totalDistance = 0.0




    private val _pet = MutableLiveData<List<Pets>>()
    val pet: LiveData<List<Pets>>
        get() = _pet


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
        getUsrPets()


    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun getAllWalks(pets: List<Pets>){

        coroutineScope.launch {

            when(val result = repository.getWalkListByUserId(userId)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE

                    val walks = result.data
                    if (walks.isNotEmpty()) {
                        totalTime = getTotalTime(walks)
                        totalDistance = getTotalDistance(walks)
                        getWalksWithPetInfo(totalTime, totalDistance, walks, pets)
                    } else {
                        //TODO
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

    private fun getTotalDistance(walks: List<Walk>): Double {
        var total = 0.0
        for (walk in walks){
            total += walk.distance ?: 0f
        }

        return total
    }

    private fun getTotalTime(walks: List<Walk>): Long {
        var total = 0L
        for (walk in walks){
            total += walk.period ?: 0
        }

        return total
    }

    private fun getWalksWithPetInfo(totalTime: Long, totalDistance: Double, walks: List<Walk>, pets: List<Pets>) {

        coroutineScope.launch {

            when(val result = repository.getWalkListInfoByWalkList(walks)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE

                    val walksWithInfo = result.data
                    calculate(totalTime, totalDistance, walksWithInfo, pets)
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

    private fun getUsrPets(){

        coroutineScope.launch {

            when (val result = repository.getAllPetsByUserId(UserManager.userUID!!)) {

                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val pets = result.data
                    getAllWalks(pets)
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

    private fun calculate(totalTime: Long, totalDistance: Double, walks: List<Walk>, pets: List<Pets>){

        val petsWithTotalInfo = mutableListOf<Pets>()

        for (pet in pets){


            for (walk in walks) {
                if (walk.petsIdList.isNotEmpty()) {
                    for (petId in walk.petsIdList) {
                        if (pet.id == petId) {
                            pet.divTotalDistance += ((walk.distance ?: 0.0).toDouble() )
                            pet.divTotalTime += walk.period ?: 0

                        }
                    }
                }
            }
            petsWithTotalInfo.add(pet)

        }

        _pet.value = petsWithTotalInfo
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