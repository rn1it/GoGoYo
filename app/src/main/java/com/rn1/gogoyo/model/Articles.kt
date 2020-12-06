package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Articles(
    var id: String = "",
    var title: String = "",
    var content: String? = null,
    var authorId: String? = null,
    val createdTime: Long? = null,
    val images: List<File>? = null,
    val like: Int? = null
): Parcelable