package com.rn1.gogoyo.walk.end

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkEndBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Points
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.util.Logger

private const val PERMISSION_ID = 1

class WalkEndFragment : Fragment() {

    private lateinit var binding:FragmentWalkEndBinding
    private val viewModel by viewModels<WalkEndViewModel> { getVmFactory(WalkEndFragmentArgs.fromBundle(requireArguments()).walkKey) }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermission = false
    private var lastKnownLocation: Location? = null
    private var myMap: GoogleMap? = null

    var walk = Walk()

    private val callback = OnMapReadyCallback { googleMap ->
        myMap = googleMap

        getLocationPermission()

        // get current location
        getDeviceLocation()

        drawLine(walk.points!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk_end, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel



        val recyclerView = binding.walkEndPetRv
        val adapter = PetAdapter()

        recyclerView.adapter = adapter

        val pet1 = Pets("001","111")
        val list = mutableListOf<Pets>()
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)
        list.add(pet1)

        adapter.submitList(list)


        viewModel.walk.observe(viewLifecycleOwner, Observer {
            it?.let {
                walk = it
            }
        })

        viewModel.petList.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.submitList(it)
            }
        })

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.walkEndMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }



    private fun drawLine(list: List<Points>){

        val pointList = mutableListOf<LatLng>()

        for (point in list) {
            val latLng = LatLng(point.latitude!!, point.longitude!!)
            pointList.add(latLng)
        }

        myMap!!.addPolyline(
            PolylineOptions()
                .color(Color.YELLOW)
                .width(15f)
                .addAll(pointList)
        )
    }

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
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            myMap?.apply {
                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), 17f)
                                )
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

}