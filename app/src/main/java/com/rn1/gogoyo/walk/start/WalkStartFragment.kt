package com.rn1.gogoyo.walk.start

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.FragmentWalkStartBinding
import com.rn1.gogoyo.ext.checkPermission
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Friends
import com.rn1.gogoyo.model.Users
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.util.Logger
import kotlinx.android.synthetic.main.qr_code_user_layout.*
import kotlinx.android.synthetic.main.qrcode_dialog.*
import kotlinx.android.synthetic.main.scan_code_layout.*
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


private const val PERMISSION_ID = 1

class WalkStartFragment : Fragment(){

    private lateinit var users: Users
    private lateinit var friends: List<Friends>
    private var filePath: String = ""
    private lateinit var codeScanner: CodeScanner

    private lateinit var binding: FragmentWalkStartBinding
    private val viewModel by viewModels<WalkStartViewModel> { getVmFactory(
        WalkStartFragmentArgs.fromBundle(
            requireArguments()
        ).petIdListKey.asList()
    ) }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermission = false
    private var lastKnownLocation: Location? = null
    private var myMap: GoogleMap? = null

    //set current location follow by time
    private var lat: Double? = null
    private var lon: Double? = null

    private val markList: MutableList<Marker> = mutableListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        myMap = googleMap
        googleMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(viewModel, requireContext()))

        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()

        viewModel.onLineWalks.observe(viewLifecycleOwner, Observer {
            it?.let {

                if (it.isNotEmpty()) {
                    removeMarkers()
                }
                createMakers(it)
            }
        })

        googleMap.setOnMarkerClickListener(OnMarkerClickListener { mark ->
            mark.showInfoWindow()
            val handler = Handler()
            handler.postDelayed(Runnable { mark.showInfoWindow() }, 200)
            true
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk_start, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

                val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                dialog.setTitle("退出將結束散步")
                dialog.setMessage("確定退出?")
                dialog.setPositiveButton("確定") { _, _ ->  viewModel.onNavigateToEndWalk()}
                dialog.setNegativeButton("取消", null)

                dialog.show()
            }
        })


        val recyclerView = binding.walkImgRv
        val adapter = WalkImgAdapter()
        recyclerView.adapter = adapter

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.let {
                users = it
            }
        })

        viewModel.liveFriend.observe(viewLifecycleOwner, Observer {
            it?.let {
                friends = it
            }
        })

        viewModel.getCurrentLocation.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    getDeviceLocation()
                    viewModel.onDoneGetCurrentLocation()
                }
            }
        })

        viewModel.navigateToEndWalk.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalWalkEndFragment(it))
                viewModel.onDoneNavigateToEndWalk()
            }
        })

        viewModel.petIdList.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("this time walking pets = $it")
            }
        })


        viewModel.imageStringList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.qrCodeUser.observe(viewLifecycleOwner, Observer {
            it?.let {

                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.qr_code_user_layout)
                dialog.show()

                val lp = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                lp.copyFrom(dialog.window!!.attributes)
                dialog.window!!.attributes = lp

                val qrCodeUserImageIv = dialog.qrCodeUserImageIv
                val qrCodeUserNameIv = dialog.qrCodeUserNameIv
                val checkQrCodeFriendBt = dialog.checkQrCodeFriendBt

                qrCodeUserNameIv.text = it.name
//                qrCodeUserImageIv.outlineProvider = MapOutlineProvider()

                val imgUri = it.image?.toUri()?.buildUpon()?.scheme("https")?.build()
                Glide.with(qrCodeUserImageIv.context)
                    .load(imgUri)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.dog_profile)
                            .error(R.drawable.my_pet))
                    .into(qrCodeUserImageIv)


                val friends = friends.filter { friend -> friend.friendId == it.id }
                if (friends.isEmpty()) {
                    checkQrCodeFriendBt.text = "加好友"
                } else {
                    val friend = friends[0]
                    when (friend.status) {
                        0 -> "已送出好友邀請"
                        1 -> "接受"
                        2 -> "朋友"
                    }
                }
            }
        })

        binding.walkFinishBt.setOnClickListener {

            val snapshotReadyCallback : GoogleMap.SnapshotReadyCallback = GoogleMap.SnapshotReadyCallback {
                viewModel.saveMap(it)
            }

            val onMapLoadedCallback : GoogleMap.OnMapLoadedCallback = GoogleMap.OnMapLoadedCallback {
                myMap!!.snapshot(snapshotReadyCallback)
            }

            myMap!!.setOnMapLoadedCallback(onMapLoadedCallback)
        }

        binding.cameraBt.setOnClickListener {
            if (viewModel.imageStringList.value!= null) {
                if (viewModel.imageStringList.value!!.size >= 3) {
                    Toast.makeText(context, "很抱歉，目前最多只能拍攝三張照片!", Toast.LENGTH_SHORT).show()
                } else {
                    checkPermission()
                }
            } else {
                checkPermission()
            }
        }

        binding.addFriendBt.setOnClickListener {
            setUpPermission()
            scan()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.walkingMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        // 2. init fusedLocationProviderClient and set LocationServices object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    // 1. check Permission and get permission
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED) {
            locationPermission = false
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
            )
        } else {
            locationPermission = true
        }
    }

    // 3. set map UI isMyLocationButton Enabled
    private fun updateLocationUI() {
        if (myMap == null) {
            return
        }
        try {
            if (locationPermission) {
                myMap!!.isMyLocationEnabled = true
                myMap!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                myMap!!.isMyLocationEnabled = false
                myMap!!.uiSettings.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // 4. get permission and update LocationUI: set map UI isMyLocationButton Enabled
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true
                }
            }
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //get image
                }
                return
            }
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission request fail", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // set map UI isMyLocationButton Enabled
        updateLocationUI()
    }

    // 5. get current location
    private fun getDeviceLocation() {
        try {
            if (locationPermission) {
                val locationRequest = fusedLocationProviderClient.lastLocation
                locationRequest.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        /**
                         * get current position success!!
                          */
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {

                            // beginning location
                            if (null == lat && null == lon) {
                                lat = lastKnownLocation!!.latitude
                                lon = lastKnownLocation!!.longitude

                                Logger.d("start: lat = $lat, lon = $lon")

                                viewModel.insertWalk(lat!!, lon!!)

                                myMap?.apply {
                                    moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                lastKnownLocation!!.latitude,
                                                lastKnownLocation!!.longitude
                                            ), 17f
                                        )
                                    )
                                }

                            } else {
                                // draw line
                                drawLine(
                                    lat!!,
                                    lon!!,
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                )

                                val dis = getDistance(
                                    LatLng(lat!!, lon!!), LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    )
                                )
                                // set currentLatLng
                                lat = lastKnownLocation!!.latitude
                                lon = lastKnownLocation!!.longitude
                                viewModel.savePoint(lat!!, lon!!, dis)
                            }

                        }
                    } else {
                        myMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun drawLine(lastLat: Double, lastLon: Double, currentLat: Double, currentLon: Double){
        // Draw polyline
        val polyline = myMap!!.addPolyline(
            PolylineOptions()
                .color(R.color.colorPrimaryDark)
                .width(15f)
                .add(
                    LatLng(lastLat, lastLon), LatLng(currentLat, currentLon)
                )
        )

        polyline.tag = "A"
    }



    private fun createMakers(list: List<Walk>){

        for (walk in list) {
            val marker = myMap?.addMarker(
                MarkerOptions().position(
                    LatLng(
                        walk.currentLat!!,
                        walk.currentLng!!
                    )
                ).title(walk.userId)
            )!!
            marker.tag = walk

            markList.add(marker)
        }
    }

    private fun removeMarkers(){
        for (mark in markList) {
            mark.remove()
        }
        markList.clear()
    }


    private fun getDistance(start: LatLng, end: LatLng): Double {

        val lat1 = Math.PI / 180 * start.latitude
        val lat2 = Math.PI / 180 * end.latitude
        val lon1 = Math.PI / 180 * start.longitude
        val lon2 = Math.PI / 180 * end.longitude

        //radius of earth
        val radius = 6371.0

        // distance between two points, return kilometer
        val d = acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1)) * radius

        return d
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                PICK_IMAGE -> {
                    filePath = ImagePicker.getFilePath(data) ?: ""
                    if (filePath.isNotEmpty()) {
                        val imgPath = filePath
                        Logger.d(" = $imgPath")
                        Toast.makeText(this.requireContext(), imgPath, Toast.LENGTH_SHORT).show()

                        viewModel.uploadImage(imgPath)

                    } else {
                        Toast.makeText(this.requireContext(), "上傳失敗", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    /**
     * marker info custom
     */
    class CustomInfoWindowForGoogleMap(val viewModel: WalkStartViewModel, context: Context) : GoogleMap.InfoWindowAdapter {

        var mContext = context
        var mWindow = (context as Activity).layoutInflater.inflate(
            R.layout.marker_info_layout,
            null
        )

        private fun resetWindowText(marker: Marker, view: View){

            view.setBackgroundResource(R.drawable.msg_bubble_custom)

            val walk = marker.tag as Walk
            val recyclerView = view.findViewById<RecyclerView>(R.id.windowInfoPetsRv)
            val adapter = WalkStartPetAdapter(viewModel)
            adapter.submitList(walk.pets)

            recyclerView.adapter = adapter


            val tvTitle = view.findViewById<TextView>(R.id.infoUserNameTv)
            tvTitle.text = walk.user!!.name

        }

        override fun getInfoContents(marker: Marker): View {
            resetWindowText(marker, mWindow)
            return mWindow
        }

        override fun getInfoWindow(marker: Marker): View? {
            resetWindowText(marker, mWindow)
            return mWindow
        }
    }

    fun getBarCode(){
        val encoder = BarcodeEncoder()

        try {
            val bitmap = encoder
                .encodeBitmap(UserManager.userUID!!, BarcodeFormat.QR_CODE, 500, 500)

            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.qrcode_dialog)
            dialog.show()

            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            lp.copyFrom(dialog.window!!.attributes)
            dialog.window!!.attributes = lp

            val qrCodeImg = dialog.findViewById(R.id.qrCodeImg) as ImageView
            qrCodeImg.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "something wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scan(){

        val alertDialog = AlertDialog.Builder(requireActivity())
        val v = layoutInflater.inflate(R.layout.scan_code_layout, null)
        alertDialog.setView(v)

        val dialog = alertDialog.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()

        val showQrCode = dialog.showQrCodeTv
        showQrCode.setOnClickListener {
            getBarCode()
        }

        val scannerView = dialog.scanner_view

        codeScanner = CodeScanner(requireContext(), scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                Logger.d(it.text)
                viewModel.getQrCodeUser(it.text)
                dialog.dismiss()
            }

            errorCallback = ErrorCallback {
            }
        }.startPreview()

    }

    fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    fun makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 200
        private const val PICK_IMAGE = 2404
        private const val CAMERA_REQUEST_CODE = 500
    }

}