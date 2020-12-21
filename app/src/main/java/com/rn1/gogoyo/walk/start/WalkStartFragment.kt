package com.rn1.gogoyo.walk.start

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkStartBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.util.Logger
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


private const val PERMISSION_ID = 1

class WalkStartFragment : Fragment(){

    private var filePath: String = ""
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

                Logger.d("current online list = $it")
                if (it.isNotEmpty()) {
                    removeMarkers()
                }
                createMakers(it)
            }
        })

//        viewModel.showMarker.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                Logger.d("aaaaaaaaaaaa")
//                it.showInfoWindow()
//            }
//        })


//        googleMap.setOnMarkerClickListener
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

        val recyclerView = binding.walkImgRv
        val adapter = WalkImgAdapter()
        recyclerView.adapter = adapter

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
            checkPermission()
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
                } else {
                    Toast.makeText(this.context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
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

//    private fun stylePolyline(polyline: Polyline) {
//        var type = ""
//        // Get the data object stored with the polyline.
//        if (polyline.tag != null) {
//            type = polyline.tag.toString()
//        }
//        polyline.setWidth(15f)
//        polyline.setColor(R.color.app_main_color)
//    }`

//    private fun saveView(view: View): Bitmap {
//        view.isDrawingCacheEnabled = true
//        view.buildDrawingCache()
//        val bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
//        view.destroyDrawingCache()
//        return bitmap
//    }


    private fun checkPermission() {
        val permission = ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //未取得權限，向使用者要求允許權限
            ActivityCompat.requestPermissions(
                this.requireActivity(), arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_EXTERNAL_STORAGE
            )
        }
        getLocalImg()
    }

    private fun getLocalImg() {
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(
            this.requireContext(),
            "resultCode = $resultCode , requestCode = $requestCode",
            Toast.LENGTH_SHORT
        ).show()

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
                        Toast.makeText(this.requireContext(), "Upload failed", Toast.LENGTH_SHORT)
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

//    class markerClickListener: GoogleMap.OnMapClickListener(){
//
//    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 200
        private const val PICK_IMAGE = 2404
    }


}