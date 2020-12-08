package com.rn1.gogoyo.walk.start

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.model.source.GogoyoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class WalkStartViewModel(
    val repository: GogoyoRepository,
    private val arguments: List<String>): ViewModel() {

    private val _petIdList = MutableLiveData<List<String>>().apply {
        value = arguments
    }

    val petIdList: LiveData<List<String>>
        get() = _petIdList

    private val _navigateToEndWalk = MutableLiveData<Boolean>()

    val navigateToEndWalk: LiveData<Boolean>
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

    init {
        startTimer()
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

    fun onDoneAddPoint(){
        _addPoint.value = null
    }

    fun onNavigateToEndWalk(){
        _navigateToEndWalk.value = true
    }

    fun onDoneNavigateToEndWalk(){
        _navigateToEndWalk.value = null
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}