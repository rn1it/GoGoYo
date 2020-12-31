package com.rn1.gogoyo.mypets.pet

import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
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
    private val mp = MediaPlayer()

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

            if (it.isNullOrEmpty()) {
                binding.noPetTv.visibility = View.VISIBLE
                binding.editPetInfoBt.visibility = View.GONE
            } else {
                binding.noPetTv.visibility = View.INVISIBLE
                adapter.submitList(it)
                // listen page change and change data for selected pet
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        stopMediaPlayer()
                        viewModel.pet.value = it[position]
                    }
                })

                binding.videoPlayBt.setOnClickListener{
                    setExoplayer(viewModel.pet.value!!.video)
                }

                binding.audioPlayBt.setOnClickListener{
                    play(viewModel.pet.value!!.voice)
                }
            }
        })

        viewModel.navigateToNewPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    if (viewModel.petList.value != null) {
                        if (viewModel.petList.value!!.size == 5) {
                            Toast.makeText(context, "很抱歉，目前最多只能註冊五隻寵物", Toast.LENGTH_SHORT).show()
                        } else {
                            findNavController().navigate(NavigationDirections.actionGlobalNewPetFragment())
                            viewModel.onDoneNavigateToNewPet()
                        }
                    } else {
                        findNavController().navigate(NavigationDirections.actionGlobalNewPetFragment())
                        viewModel.onDoneNavigateToNewPet()
                    }
                }
            }
        })

        viewModel.editPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalEditPetFragment(it))
                viewModel.toEditPetDone()
            }
        })

        setUpViewPager(viewPager)

        return binding.root
    }

    /**
     * for video play
     */
    private fun setExoplayer(url: String?) {

        stopMediaPlayer()

        if (url.isNullOrEmpty()) {
            Toast.makeText(context, "還沒有上傳影片喔!", Toast.LENGTH_SHORT).show()
        } else {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.introvid)
            dialog.show()

            val lp = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            lp.copyFrom(dialog.window!!.attributes)
            dialog.window!!.attributes = lp

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
                Toast.makeText(context, "影片播放失敗，請聯絡開發人員", Toast.LENGTH_SHORT).show()
                Log.e("ViewHolder", "exoplayer error$e")
            }
        }
    }

    /**
     * for audio play
     */
    private fun play(path: String?) {
        stopMediaPlayer()
        if (path.isNullOrBlank()) {
                Toast.makeText(context, "還沒有上傳聲音喔!", Toast.LENGTH_SHORT).show()
        } else {
            Logger.d("play path = $path")
            val uri = Uri.parse(path)
            mp.reset()
            mp.setDataSource(requireContext(), uri)
            mp.setOnPreparedListener {
                it.start()
                binding.audioPlayBt.setImageResource(R.drawable.pause_24)
            }
            mp.setOnCompletionListener {
                binding.audioPlayBt.setImageResource(R.drawable.play_music)
            }
            mp.prepare()
        }

    }

    /**
     * for audio stop
     */
    private fun stopMediaPlayer() {
        if (mp.isPlaying) {
            mp.stop()
            binding.audioPlayBt.setImageResource(R.drawable.play_music)
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

    override fun onPause() {
        super.onPause()
        stopMediaPlayer()
        Logger.d("onPause stopMediaPlayer")
    }
}