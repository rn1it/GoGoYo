package com.rn1.gogoyo.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.rn1.gogoyo.GogoyoApplication

object Util {

    fun isInternetConnected(): Boolean {
        val cm = GogoyoApplication.instance
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

}