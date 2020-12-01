package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Articles(
    val id: String,
    val title: String,
    val content: String? = null,
    val authorId: String? = null,
    val createdTime: Long? = null,
    val images: List<File>? = null,
    val like: Int? = null
): Parcelable