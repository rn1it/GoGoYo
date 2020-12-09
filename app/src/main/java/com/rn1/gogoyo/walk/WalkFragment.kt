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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentWalkBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.model.Pets


class WalkFragment : Fragment() {

    private lateinit var binding: FragmentWalkBinding
    private val viewModel by viewModels<WalkViewModel> { getVmFactory() }

    private val callback = OnMapReadyCallback { googleMap ->

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val recyclerView = binding.walkDogSelectRv
        val adapter = WalkPetAdapter(viewModel)
        recyclerView.adapter = adapter

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.walkEndMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}