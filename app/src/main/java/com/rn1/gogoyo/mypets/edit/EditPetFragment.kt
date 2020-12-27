package com.rn1.gogoyo.mypets.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.mypets.newpets.NewPetViewModel
import com.rn1.gogoyo.util.Logger
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
                    NewPetViewModel.INVALID_FORMAT_NAME_EMPTY -> Toast.makeText(
                        context,
                        "寵物姓名不可以空白!",
                        Toast.LENGTH_SHORT
                    ).show()
                    NewPetViewModel.INVALID_FORMAT_INTRODUCTION_EMPTY -> Toast.makeText(
                        context,
                        "來段簡短的介紹讓大家更認識你!",
                        Toast.LENGTH_SHORT
                    ).show()
                    NewPetViewModel.INVALID_FORMAT_SEX_EMPTY -> Toast.makeText(
                        context,
                        "記得選擇寵物性別喔!",
                        Toast.LENGTH_SHORT
                    ).show()
                    NewPetViewModel.INVALID_IMAGE_PATH_EMPTY -> Toast.makeText(
                        context,
                        "別忘記上傳一張寵物的萌照啊!",
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
        Toast.makeText(this.requireContext(), "resultCode = $resultCode , requestCode = $requestCode", Toast.LENGTH_SHORT).show()
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                PICK_IMAGE -> {
                    filePath = ImagePicker.getFilePath(data) ?: ""
                    if (filePath.isNotEmpty()) {
                        val imgPath = filePath
                        Logger.d(" = $imgPath")
                        Toast.makeText(this.requireContext(), imgPath, Toast.LENGTH_SHORT).show()
                        Glide.with(this.requireContext()).load(filePath).into(binding.uploadPetIv)

                        viewModel.uploadImage(imgPath)

                    } else {
                        Toast.makeText(this.requireContext(), "Upload failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                PICK_VIDEO -> {
                    if (data?.data != null) {
                        val uri = data.data!!
                        Toast.makeText(this.requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()

                        viewModel.uploadVideo(uri)
                    }
                }

                PICK_AUDIO -> {
                    if (data?.data != null) {
                        val uri = data.data!!
                        Toast.makeText(this.requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()

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