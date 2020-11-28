package com.rn1.gogoyo.walk

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkBinding
import com.rn1.gogoyo.ext.getVmFactory


class WalkFragment : Fragment() {

    private lateinit var binding: FragmentWalkBinding
    private val viewModel by viewModels<WalkViewModel> { getVmFactory() }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

//        var lat: Double
//        val lng: Double
//        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val location: Location? =
//            lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) // 設定定位資訊由 GPS提供
//
//        lat = location.getLatitude() // 取得經度
//
//        lng = location.getLongitude() // 取得緯度
//
//        val HOME =
//            LatLng(lat, lng)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HOME, 15.0f)) //數字越大放越大


        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}