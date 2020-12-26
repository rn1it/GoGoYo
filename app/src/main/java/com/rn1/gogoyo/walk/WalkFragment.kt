package com.rn1.gogoyo.walk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.service.WalkTimerService
import com.rn1.gogoyo.util.Logger

private const val PERMISSION_ID = 1
class WalkFragment : Fragment() {

    private lateinit var binding: FragmentWalkBinding
    private val viewModel by viewModels<WalkViewModel> { getVmFactory() }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermission = false
    private var lastKnownLocation: Location? = null
    private var myMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        myMap = googleMap

        getLocationPermission()
        // get current location
        getDeviceLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val recyclerView = binding.walkDogSelectRv
        val adapter = WalkPetAdapter(viewModel)
        recyclerView.adapter = adapter

        viewModel.weatherDescription.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.d("it = $it")

                when(it) {
                    "clouds" -> {
                        binding.lottieAnimationView.setAnimation(R.raw.clouds_2)
                        binding.lottieAnimationView.playAnimation()
                        binding.lottieAnimationView.loop(true)
//                        binding.lottieAnimationView2.visibility = View.INVISIBLE
                    }
                    "sun" -> {
                        Logger.d("sun = $it")
                        binding.lottieAnimationView.setAnimation(R.raw.sunny_day)
                        binding.lottieAnimationView.playAnimation()
                        binding.lottieAnimationView.loop(true)
//                        binding.lottieAnimationView2.visibility = View.INVISIBLE
                    }
                    "rain" -> {
                        Logger.d("rain = $it")
                        binding.lottieAnimationView.setAnimation(R.raw.rain_umbrella_bg)
                        binding.lottieAnimationView2.setAnimation(R.raw.rain_umbrella_bg)
                        binding.lottieAnimationView.playAnimation()
                        binding.lottieAnimationView.loop(true)
                        binding.lottieAnimationView2.playAnimation()
                        binding.lottieAnimationView2.loop(true)
                    }
                }
            }
        })

        viewModel.userPetList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToStartWalk.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalWalkStartFragment(it.toTypedArray()))
                viewModel.onDoneNavigateToStartWalk()
            }
        })

//        binding.button6.setOnClickListener {
//            val intent = Intent(context, WalkTimerService::class.java)
//            requireContext().startService(intent)
//        }
//
//        binding.button7.setOnClickListener {
//            val intent = Intent(context, WalkTimerService::class.java)
//            requireContext().stopService(intent)
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mapFragment = childFragmentManager.findFragmentById(R.id.walkEndMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
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
                    // set map UI isMyLocationButton Enabled
                    updateLocationUI()
                }
            }
        }

    }

    private fun getDeviceLocation() {
        try {
            if (locationPermission) {
                val locationRequest = fusedLocationProviderClient.lastLocation
                locationRequest.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            myMap?.apply {
                                addMarker(MarkerOptions().position(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)))
                                viewModel.getCurrentWeather(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), 13f)
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