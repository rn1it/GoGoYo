package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArticleMessages(
    var id: String = "",
    var userId: String =  "",
    var content: String = "",
    val createdTime: Long = -1,
): Parcelable
