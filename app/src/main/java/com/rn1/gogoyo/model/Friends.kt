package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friends(
    var id: String = "",
    var friendId: String = "",
    //(0:邀請中, 1:被邀請, 2: 好友關係)
    var status: Int? = null,
    var createdTime: Long = -1
): Parcelable