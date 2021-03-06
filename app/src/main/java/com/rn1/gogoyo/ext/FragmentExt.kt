package com.rn1.gogoyo.ext

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.factory.*
import com.rn1.gogoyo.model.Articles
import com.rn1.gogoyo.model.Chatroom
import com.rn1.gogoyo.model.Walk
import com.rn1.gogoyo.util.REQUEST_EXTERNAL_STORAGE

fun Fragment.getVmFactory(): ViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return ViewModelFactory(repository)
}

fun Fragment.getVmFactory(articles: Articles): ArticleViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return ArticleViewModelFactory(repository, articles)
}

fun Fragment.getVmFactory(petIdList: List<String>): WalkStartViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return WalkStartViewModelFactory(repository, petIdList)
}

fun Fragment.getVmFactory(id: String): IdStringViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return IdStringViewModelFactory(repository, id)
}

fun Fragment.getVmFactory(walk: Walk): WalkDataViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return WalkDataViewModelFactory(repository, walk)
}

fun Fragment.getVmFactory(chatRoom: Chatroom): ChatRoomViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return ChatRoomViewModelFactory(repository, chatRoom)
}

fun Fragment.checkPermission() {
    val permission = ActivityCompat.checkSelfPermission(
        GogoyoApplication.instance.applicationContext,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    if (permission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_EXTERNAL_STORAGE
        )
    }
    getLocalImg(this)
}

/**
 * Crop image (Optional), Check Customization for more option
 * compress, Final image size will be less than 1 MB (Optional)
 * ResultSize, Final image resolution will be less than 1080 x 1080 (Optional)
 */
private fun getLocalImg(fragment: Fragment) {
    ImagePicker.with(fragment)
        .crop()
        .compress(1024)
        .maxResultSize(
            1080,
            1080
        )
        .start()
}
