package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Points(
    var latitude : Double? = null,
    var longitude: Double? = null,
    val createdTime: Long = Calendar.getInstance().timeInMillis
): Parcelable