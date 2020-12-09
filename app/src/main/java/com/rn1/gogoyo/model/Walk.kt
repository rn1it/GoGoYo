package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Walk(
    var id: String = "",
    var userId: String = "",
    var petsIdList: List<String>? = null,
    var newFriend: List<Friends>? = null,
    var points : List<Points>? = null,
    var period : Long? = null,
    var distance: Float? = null,
    val startTime: Long = Calendar.getInstance().timeInMillis,
    var endTime: Long? = null,
    var images: List<String>? = null,
    var createdTime: Long? = null
): Parcelable