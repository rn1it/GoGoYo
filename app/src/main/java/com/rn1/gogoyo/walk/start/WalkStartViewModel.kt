package com.rn1.gogoyo.walk.start

import android.graphics.Bitmap
import android.hardware.usb.UsbRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.auth.User
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.model.*
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.util.LoadStatus
import com.rn1.gogoyo.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class WalkStartViewModel(
    val repository: GogoyoRepository,
    private val arguments: List<String>
    ): ViewModel() {

    private var mapImgPath = ""

    val outlineProvider = MapOutlineProvider()

    private val _user = MutableLiveData<Users>()
    val user: LiveData<Users>
        get() = _user

    var liveFriend = MutableLiveData<List<Friends>>()

    private val _walk =  MutableLiveData<Walk>()
    val walk: LiveData<Walk>
        get() = _walk

    private var totalDistance = 0.0

    private val _onLineWalks = MutableLiveData<List<Walk>>()

    val onLineWalks: LiveData<List<Walk>>
        get() = _onLineWalks

    private val pointsList: MutableList<Points> = mutableListOf()

    private val _petIdList = MutableLiveData<List<String>>().apply {
        value = arguments
    }

    val petIdList: LiveData<List<String>>
        get() = _petIdList

    private val _showMarker = MutableLiveData<Marker>()
    val showMarker: LiveData<Marker>
        get() = _showMarker

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

    private val _getCurrentLocation = MutableLiveData<Boolean>()

    val getCurrentLocation: LiveData<Boolean>
        get() = _getCurrentLocation

    private lateinit var timer : Timer
    private var second = 0
    private var stopCount = true

    private val _imageStringList = MutableLiveData<List<String>>()
    val imageStringList: LiveData<List<String>>
        get() = _imageStringList

    private val _qrCodeUser = MutableLiveData<Users>()
    val qrCodeUser: LiveData<Users>
        get() = _qrCodeUser

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadStatus>()

    val status: LiveData<LoadStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {

        getUser()
        getUserLiveFriend()
        startTimer()
        _imageStringList.value = mutableListOf()
        // set user isWalking = true
        setUserWalkingStatus(true)
        getOnlineWalkList()
//        getLiveWalkingList()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        viewModelJob.cancel()
    }

    private fun getUser() {
        coroutineScope.launch {

            _user.value =  when (val result = repository.getUserById(UserManager.userUID!!)) {
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

    private fun getUserLiveFriend(){
        liveFriend = repository.getUserLiveFriend(UserManager.userUID!!, 2)
    }



    private fun startTimer(){
        timer = Timer()
        timer.schedule(1000, 1000) {
            second += 1

            // can bot set value on a background thread
            viewModelScope.launch(Dispatchers.Main) {

                // 5 second draw polyline
                if (second % 5 == 0) {
                    _getCurrentLocation.value = true
                }
                if (second % 30 == 0){
                    // 30 second refresh online walk
                    getOnlineWalkList()
                }

                formatTime(second)
            }
        }
    }

    fun onDoneGetCurrentLocation(){
        _getCurrentLocation.value = null
    }

    fun savePoint(lat: Double, lng: Double, dis: Double) {

        totalDistance += dis

        val point = Points()
        point.latitude = lat
        point.longitude = lng
        pointsList.add(point)

        val walk = walk.value!!
        walk.apply {
            currentLat = lat
            currentLng = lng
            points = pointsList
        }

        // update point
        coroutineScope.launch {

            _walk.value =  when (val result = repository.updateWalk(walk)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("result.data = ${result.data}")
//                    setUserWalkingStatus(false)
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

    fun insertWalk(lat: Double, lng:Double){

        coroutineScope.launch {

            val walk = Walk().apply {
                userId = UserManager.userUID!!
                startTime = Calendar.getInstance().timeInMillis
                petsIdList = arguments
                period = second.toLong()
                currentLat = lat
                currentLng = lng
            }

            val point = Points()
            point.latitude = lat
            point.longitude = lng
            pointsList.add(point)

            _walk.value =  when (val result = repository.insertWalk(walk)) {
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

            val walk = walk.value!!

            walk.apply {
                endTime = Calendar.getInstance().timeInMillis
                points = pointsList
                period = second.toLong()
                mapImg = mapImgPath
                distance = totalDistance.toFloat()
                images = _imageStringList.value
            }

            _navigateToEndWalk.value = when (val result = repository.updateWalk(walk)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("result.data = ${result.data}")
                    setUserWalkingStatus(false)
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

    private fun setUserWalkingStatus(isWalking: Boolean){

        coroutineScope.launch {

            when (val result = repository.setWalkingStatus(UserManager.userUID!!, isWalking)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE

                    if (isWalking) {
                        Logger.d("user is walking!")
                    } else {
                        Logger.d("walk finished!")
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

    private fun getOnlineWalkList(){
        coroutineScope.launch {

            when (val result = repository.getOthersWalkingList(UserManager.userUID!!)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    val walks = result.data
                    if (walks.isNotEmpty()){
                        getOnlineWalkWithInfo(walks)
                    } else {
                        _onLineWalks.value = null
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

    private fun getOnlineWalkWithInfo(walks: List<Walk>){

        coroutineScope.launch {

            when (val result = repository.getWalkListInfoByWalkList(walks)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("getOnlineWalkList Second = $second, result.data = ${result.data}")
                    getOnlineWalkWithUserInfo(result.data)
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    _onLineWalks.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    _onLineWalks.value = null
                }
                else -> {
                    _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                    _status.value = LoadStatus.ERROR
                    _onLineWalks.value = null
                }
            }
        }


    }

    private fun getOnlineWalkWithUserInfo(walks: List<Walk>){

        coroutineScope.launch {

            when (val result = repository.getWalkListUserInfoByWalkList(walks)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("getOnlineWalkList Second = $second, result.data = ${result.data}")
                    _onLineWalks.value = result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    _onLineWalks.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    _onLineWalks.value = null
                }
                else -> {
                    _error.value = GogoyoApplication.instance.getString(R.string.something_wrong)
                    _status.value = LoadStatus.ERROR
                    _onLineWalks.value = null
                }
            }
        }
    }

    fun uploadImage(path: String){
        coroutineScope.launch {

            when (val result = repository.getImageUri(path)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("uri = ${result.data}")
                    val list = mutableListOf<String>()
                    list.addAll(_imageStringList.value!!)
                    list.add(result.data)
                    _imageStringList.value = list

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

    fun saveMap(bitmap: Bitmap) {

        val f = File(GogoyoApplication.instance.cacheDir, System.currentTimeMillis().toString());
        f.createNewFile();

        val bos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        val bitmapData = bos.toByteArray();

        val fos = FileOutputStream(f);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        uploadMapImage(f.path)

    }

    private fun uploadMapImage(path: String){
        timer.cancel()
        coroutineScope.launch {

            when (val result = repository.getImageUri(path)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    Logger.d("uri = ${result.data}")
                    mapImgPath = result.data
                    onNavigateToEndWalk()
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

    fun getQrCodeUser(id: String){
        coroutineScope.launch {

            _qrCodeUser.value = when(val result = repository.getUsersById(listOf(id))) {

                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data[0]
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

}