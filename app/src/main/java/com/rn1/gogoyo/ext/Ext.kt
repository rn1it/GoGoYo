package com.rn1.gogoyo.ext

import android.icu.text.SimpleDateFormat
import java.util.*

fun Long.toDisplayFormat(): String {
    return SimpleDateFormat("yyyy.MM.dd hh:mm", Locale.TAIWAN).format(this)
}