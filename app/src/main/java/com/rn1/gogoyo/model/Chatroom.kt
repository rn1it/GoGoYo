package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chatroom(
    val id: String = "",
    val userList: List<String> = mutableListOf(),
    val msgTime: Long = -1,
    val lastMsg: String? = "",
    var lastSenderId: String? = ""
): Parcelable