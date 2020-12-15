package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var id: String = "",
    var name: String = "",
    var introduction: String = "",
    var image: String? = null,
    val petIdList: List<String> = mutableListOf(),
    val location: String? = null,
    val qrCode: String? = null,
    val currentLat: Double? = null,
    val currentLon: Double? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    var isWalking: Boolean = false,
    val friendList: List<Friends> = mutableListOf(),
    var pets: List<Pets> = mutableListOf()
): Parcelable