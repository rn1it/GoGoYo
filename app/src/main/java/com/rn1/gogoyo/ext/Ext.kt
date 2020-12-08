package com.rn1.gogoyo.ext

import android.icu.text.SimpleDateFormat
import com.rn1.gogoyo.GogoyoApplication
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
}

fun getColor(resourceId: Int): Int {
    return GogoyoApplication.instance.getColor(resourceId)
}