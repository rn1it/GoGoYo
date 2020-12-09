package com.rn1.gogoyo.walk.start

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkStartBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.Logger
import kotlinx.android.synthetic.main.activity_login.*


private const val PERMISSION_ID = 1

class WalkStartFragment : Fragment() {

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

    private val pointsList: MutableList<Double> = mutableListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        myMap = googleMap

//        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
//            override fun getInfoWindow(marker: Marker?): View? {
//                return null
//            }
//
//            // custom info layout
//            override fun getInfoContents(marker: Marker?): View {
//                val view: View = layoutInflater.inflate(R.layout.marker_info_layout, null)
//                val  latLng = marker?.position
////                val icon = view.findViewById<ImageView>(R.id.icon)
//                val title = view.findViewById<TextView>(R.id.title)
//                val snippet = view.findViewById<TextView>(R.id.snippet)
//                val lat = view.findViewById<TextView>(R.id.lat)
//                val lng = view.findViewById<TextView>(R.id.lng)
//                title.text = marker?.title
//                snippet.text = marker?.snippet
//                lat.text = "Latitude：" + latLng?.latitude
//                lng.text = "Longitude：" + latLng?.longitude
//                return view
//            }
//        })

        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // get current location
        getDeviceLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk_start, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.navigateToEndWalk.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalWalkEndFragment(it))
                viewModel.onDoneNavigateToEndWalk()
            }
        })

        viewModel.addPoint.observe(viewLifecycleOwner, Observer {
            it?.let {
                getDeviceLocation()
                viewModel.onDoneAddPoint()
            }
        })

        viewModel.petIdList.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("it = $it")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.walkEndMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // 2. init fusedLocationProviderClient and set LocationServices object
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    // 1. check Permission and get user get permission
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

                            if (null == lat && lon == lon) {
                                lat = lastKnownLocation!!.latitude
                                lon = lastKnownLocation!!.longitude

                                Logger.d("start: lat = $lat, lon = $lon")

                                viewModel.savePoint(lat!!, lon!!)

                                myMap?.apply { addMarker(
                                    MarkerOptions()
                                    .position(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude))
                                    .title("")
                                    .snippet("${lastKnownLocation!!.latitude}, ${lastKnownLocation!!.longitude}")
                                    .anchor(0.5f, 0.5f)
//                                  .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    )

                                    moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), 17f)
                                    )
                                }

                            } else {
                                drawLine(
                                    lat!!,
                                    lon!!,
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                )

                                lat = lastKnownLocation!!.latitude
                                lon = lastKnownLocation!!.longitude

                                viewModel.savePoint(lat!!, lon!!)
                                Logger.d("current at : lat = $lat, lon = $lon")
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
                .color(Color.YELLOW)
                .width(15f)
                .add(
                    LatLng(lastLat, lastLon), LatLng(currentLat, currentLon)
                )
        )

        polyline.tag = "A"
//        stylePolyline(polyline)
    }

    private fun stylePolyline(polyline: Polyline) {
        var type = ""
        // Get the data object stored with the polyline.
        if (polyline.tag != null) {
            type = polyline.tag.toString()
        }
//        when (type) {
//            "A" ->             // Use a custom bitmap as the cap at the start of the line.
//                polyline.setStartCap(
//                    CustomCap(
//                        BitmapDescriptorFactory.fromResource(R.drawable.check), 10F
//                    )
//                )
//            "B" ->             // Use a round cap at the start of the line.
//                polyline.setStartCap(RoundCap())
//        }
//        polyline.setEndCap(RoundCap())
        polyline.setWidth(15f)
        polyline.setColor(R.color.app_main_color)
//        polyline.setJointType(JointType.ROUND)
    }

}