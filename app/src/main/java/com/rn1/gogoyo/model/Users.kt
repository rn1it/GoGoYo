package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var id: String = "",
    val name: String = "",
    val petList: List<Pets>? = null,
    val location: String? = null,
    val qrCode: String? = null,
    val currentLat: Double? = null,
    val currentLon: Double? = null,
    val isWalking: Boolean = false
): Parcelable