package com.rn1.gogoyo.walk.start

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.model.Points
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class WalkStartViewModel(
    val repository: GogoyoRepository,
    private val arguments: List<String>
    ): ViewModel() {

    private val walk = Walk()

    private val pointsList :MutableList<Points> = mutableListOf()

    private val _petIdList = MutableLiveData<List<String>>().apply {
        value = arguments
    }

    val petIdList: LiveData<List<String>>
        get() = _petIdList

    private val _navigateToEndWalk = MutableLiveData<Walk>()

    val navigateToEndWalk: LiveData<Walk>
        get() = _navigateToEndWalk

    private val _currentTime = MutableLiveData<String>().apply {
        value = GogoyoApplication.instance.getString(R.string.time_format)
    }

    val currentTime: LiveData<String>
    get() = _currentTime

    private val _counterBtString = MutableLiveData<String>().apply {
        value = GogoyoApplication.instance.getString(R.string.stop_count)
    }

    val counterBtString: LiveData<String>
        get() = _counterBtString

    private val _addPoint = MutableLiveData<Boolean>()

    val addPoint: LiveData<Boolean>
        get() = _addPoint



    private lateinit var timer : Timer
    private var second = 0
    private var stopCount = true


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
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        viewModelJob.cancel()
    }

    private fun startTimer(){
        timer = Timer()
        timer.schedule(1000, 1000) {
            second += 1

            // can bot set value on a background thread
            viewModelScope.launch(Dispatchers.Main) {

                // 5 second draw polyline
                if (second % 5 == 0) {
                    _addPoint.value = true
                }

                formatTime(second)
            }
        }
    }

    fun savePoint(lat: Double, lng: Double) {
        val point = Points()
        point.latitude = lat
        point.longitude = lng

        pointsList.add(point)
    }

    fun onDoneAddPoint(){
        _addPoint.value = null
    }

    fun stopOrContinueCounter(){
        if (stopCount) {
            timer.cancel()
            _counterBtString.value = GogoyoApplication.instance.getString(R.string.countinue_count)
            stopCount = false
        } else {
            startTimer()
            _counterBtString.value = GogoyoApplication.instance.getString(R.string.stop_count)
            stopCount = true
        }
    }

    private fun formatTime(second: Int){

        val hour = second / 3600
        var secondTime = second % 3600
        val minute = secondTime / 60
        secondTime %= 60

        _currentTime.value = "${addZero(hour)}:${addZero(minute)}:${addZero(secondTime)}"
    }

    private fun addZero(number: Int): String{
        return if(number.toString().length == 1){
            "0$number"
        } else {
            "$number"
        }
    }

    fun onNavigateToEndWalk(){

        coroutineScope.launch {

            walk.apply {
                userId = UserManager.userUID!!
                endTime = Calendar.getInstance().timeInMillis
                petsIdList = arguments
                points = pointsList
                period = second.toLong()
            }

            _navigateToEndWalk.value = when (val result = repository.insertWalk(walk)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("result.data = ${result.data}")
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

    fun onDoneNavigateToEndWalk(){
        _navigateToEndWalk.value = null
    }

}