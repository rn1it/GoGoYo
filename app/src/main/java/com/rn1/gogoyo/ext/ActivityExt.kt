package com.rn1.gogoyo.ext

import android.app.Activity
import com.rn1.gogoyo.GogoyoApplication
import com.rn1.gogoyo.factory.ViewModelFactory

/**
 * Extension functions for Activity
 *
 */

fun Activity.getVmFactory(): ViewModelFactory{
    val repository = (applicationContext as GogoyoApplication).repository
    return ViewModelFactory(repository)
}

