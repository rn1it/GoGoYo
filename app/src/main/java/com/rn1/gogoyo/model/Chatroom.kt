package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chatroom(
    val id: String = "",
    val userList: List<String> = mutableListOf(),
    var msgTime: Long = -1,
    var lastMsg: String? = "",
    var lastSenderId: String? = "",
    var friend: Users? = null

): Parcelable