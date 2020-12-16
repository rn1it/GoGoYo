package com.rn1.gogoyo.mypets.pet

import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentProfilePetBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.Logger
import kotlin.math.abs


class ProfilePetFragment(val userId: String) : Fragment() {

    private lateinit var binding: FragmentProfilePetBinding
    private val viewModel by viewModels<ProfilePetViewModel> { getVmFactory(userId) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_pet, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val viewPager = binding.myPetsViewPager

        val adapter = MyPetsPagerAdapter()
        viewPager.adapter = adapter

        viewModel.petList.observe(viewLifecycleOwner, Observer {
            it?.let {

                adapter.submitList(it)
                // listen page change and change data for selected pet
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        viewModel.pet.value = it[position]
                    }
                })
            }
        })

        viewModel.navigateToNewPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) findNavController().navigate(NavigationDirections.actionGlobalNewPetFragment())
                viewModel.onDoneNavigateToNewPet()
            }
        })

        viewModel.editPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalEditPetFragment(it))
                viewModel.toEditPetDone()
            }
        })

        binding.videoPlayBt.setOnClickListener{
            setExoplayer(viewModel.pet.value!!.video)
        }

        binding.audioPlayBt.setOnClickListener{
            play(viewModel.pet.value!!.voice)
        }

        setUpViewPager(viewPager)

        return binding.root
    }

    /**
     * for video play
     */
    private fun setExoplayer(url: String?) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.introvid)
        dialog.show()
        val playerView = dialog.findViewById(R.id.exoplayer_item) as PlayerView

        try {
            val exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())
            val video = Uri.parse(url)
            val dataSourceFactory = DefaultHttpDataSourceFactory("video")
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
            val mediaSource: MediaSource =
                ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null)
            playerView.player = exoPlayer
            exoPlayer.prepare(mediaSource)
            exoPlayer.playWhenReady = false
        } catch (e: Exception) {
            Log.e("ViewHolder", "exoplayer error$e")
        }
    }

    /**
     * for audio play
     */
    private fun play(path: String?) {
        try {
            Logger.d("play path = $path")
            val mp = MediaPlayer()
            mp.setDataSource(path)
            mp.setOnPreparedListener {
                it.start()
            }
            mp.prepare()
        } catch (e: java.lang.Exception) {
            Logger.d("play fail")
            e.printStackTrace()
        }
    }


    private fun setUpViewPager(viewPager: ViewPager2){
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        // not to show slide to end effect
        viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        val transformer = CompositePageTransformer()
        //transformer.addTransformer(MarginPageTransformer(8))
        transformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val v = 1 - abs(position)
            page.scaleY = 0.6f + v * 0.4f
            page.scaleX = 0.6f + v * 0.4f
        })

        viewPager.setPageTransformer(transformer)
    }
}