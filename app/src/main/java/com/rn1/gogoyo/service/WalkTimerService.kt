package com.rn1.gogoyo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class WalkTimerService: Service() {

    companion object{
        private val ACTION_START = "com.rn1.gogoyo.service.WalkTimerService_START"
        private val ACTION_STOP = "com.rn1.gogoyo.service.WalkTimerService_STOP"
    }

    enum class State{
        Start,
        Stop
    }

    private lateinit var timer : Timer
    private var second = 0
    private var stopCount = true


    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val action = intent.action

        if (action.equals(ACTION_START)) {
            timer.cancel()


        } else if (action.equals((ACTION_STOP))){


        }

        return START_NOT_STICKY
    }

    private fun startTimer(){

        timer = Timer()
        timer.schedule(1000, 1000) {
            second += 1

//            // can bot set value on a background thread
//            viewModelScope.launch(Dispatchers.Main) {
//
//                // 5 second draw polyline
//                if (second % 5 == 0) {
//                    _getCurrentLocation.value = true
//                }
//                if (second % 30 == 0){
//                    // 30 second refresh online walk
//                    getOnlineWalkList()
//                }
//
//                formatTime(second)
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}