package com.rn1.gogoyo.ext

import androidx.fragment.app.Fragment
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory{
    val repository = (requireContext().applicationContext as GogoyoApplication).repository
    return ViewModelFactory(repository)
}