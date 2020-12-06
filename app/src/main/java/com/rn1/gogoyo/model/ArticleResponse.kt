package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArticleResponse(
    var id: String = "",
    var userId: String =  "",
    var content: String = "",
    var createdTime: Long = -1,
): Parcelable
