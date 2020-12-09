package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friends(
    val user: Users? = null,
    //(0:沒關係, 1: 邀請中, 2: 好友關係)
    val status: Int? = null
): Parcelable