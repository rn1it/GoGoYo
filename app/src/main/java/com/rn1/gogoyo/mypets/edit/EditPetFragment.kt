package com.rn1.gogoyo.mypets.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.rn1.gogoyo.NavigationDirections
import com.rn1.gogoyo.R
import com.rn1.gogoyo.UserManager
import com.rn1.gogoyo.databinding.FragmentEditPetBinding
import com.rn1.gogoyo.ext.checkPermission
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.util.*
import java.lang.Exception


class EditPetFragment : Fragment() {

    private var filePath: String = ""
    private lateinit var binding: FragmentEditPetBinding
    private val viewModel by viewModels<EditPetViewModel> { getVmFactory(
        EditPetFragmentArgs.fromBundle(
            requireArguments()
        ).petIdKey
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_pet, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.canEditPet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) viewModel.editPet()
            }
        })

        viewModel.invalidInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    INVALID_FORMAT_NAME_EMPTY -> Toast.makeText(
                        context,
                        getString(R.string.empty_pet_name_text),
                        Toast.LENGTH_SHORT
                    ).show()
                    INVALID_FORMAT_INTRODUCTION_EMPTY -> Toast.makeText(
                        context,
                        getString(R.string.empty_introduction_text),
                        Toast.LENGTH_SHORT
                    ).show()
                    INVALID_FORMAT_SEX_EMPTY -> Toast.makeText(
                        context,
                        getString(R.string.empty_pet_gender_text),
                        Toast.LENGTH_SHORT
                    ).show()
                    INVALID_IMAGE_PATH_EMPTY -> Toast.makeText(
                        context,
                        getString(R.string.empty_pet_image_text),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        viewModel.navigateToPets.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(context, "資料修改成功!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        NavigationDirections.actionGlobalMyPetsFragment(
                            UserManager.userUID!!
                        )
                    )
                }
                viewModel.onDoneNavigateToPet()
            }
        })

        viewModel.showProgressBar.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    binding.progressBarMain.visibility = View.VISIBLE
                    binding.loadingTv.visibility = View.VISIBLE
                    Toast.makeText(context, "上傳中，請耐心等候...", Toast.LENGTH_LONG).show()
                } else {
                    binding.progressBarMain.visibility = View.GONE
                    binding.loadingTv.visibility = View.GONE
                    Toast.makeText(context, "影片上傳結束!", Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.showUploadingToast.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(context, "上傳進行中，請等待上傳結束!", Toast.LENGTH_LONG).show()
                    viewModel.onDoneShowUploadingToast()
                }
            }
        })

        binding.uploadPetIv.setOnClickListener {
            checkPermission()
        }

        // video upload
        binding.uploadVideoBt.setOnClickListener {
            chooseVideoFromGallery()
        }

        binding.uploadAudioBt.setOnClickListener {
            chooseAudioUpload()
        }

        return binding.root
    }

    private fun chooseVideoFromGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_VIDEO)
    }

    private fun chooseAudioUpload(){
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_AUDIO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //get image
                } else {
                    Toast.makeText(this.context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("edit pet: resultCode = $resultCode , requestCode = $requestCode")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                PICK_IMAGE -> {
                    filePath = ImagePicker.getFilePath(data) ?: ""
                    if (filePath.isNotEmpty()) {
                        val imgPath = filePath
                        Logger.d("edit pet: PICK_IMAGE => imgPath = $imgPath")
                        Glide.with(this.requireContext()).load(filePath).into(binding.uploadPetIv)

                        viewModel.uploadImage(imgPath)

                    } else {
                        Logger.d("Camera Task Cancelled")
                    }
                }

                PICK_VIDEO -> {
                    if (data?.data != null) {
                        val uri = data.data!!
                        Logger.d("edit pet: PICK_VIDEO => uri = $uri")

                        viewModel.uploadVideo(uri)
                    }
                }

                PICK_AUDIO -> {
                    if (data?.data != null) {
                        val uri = data.data!!
                        Logger.d("edit pet: PICK_AUDIO => uri = $uri")

                        viewModel.uploadAudio(uri)
                    }
                }
            }
        }
    }

    /**
     * for audio play
     */
    private fun play(path: String) {
        try {
            Logger.d("play path = $path")
            val mp = MediaPlayer()
            mp.setDataSource(path)
            mp.setOnPreparedListener {
                it.start()
            }
            mp.prepare()
        } catch (e: Exception) {
            Logger.d("play fail")
            e.printStackTrace()
        }
    }

    /**
     * for video play
     */
//    private fun setExoplayer(url: String?) {
//        val playerView = binding.exoplayerItem
//        try {
//            val exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext())
//            val video = Uri.parse(url)
//            val dataSourceFactory = DefaultHttpDataSourceFactory("video")
//            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()
//            val mediaSource: MediaSource =
//                ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null)
//            playerView.player = exoPlayer
//            exoPlayer.prepare(mediaSource)
//            exoPlayer.playWhenReady = false
//        } catch (e: Exception) {
//            Log.e("ViewHolder", "exoplayer error$e")
//        }
//    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 200
        private const val PICK_IMAGE = 2404
        private const val PICK_VIDEO = 300
        private const val PICK_AUDIO = 400
    }
}