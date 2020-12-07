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
    var userName: String? = null,
    var userImg: String? = null,
    var createdTime: Long = -1,
    val images: List<File>? = null,
    val like: Int = 0,
    val responseList: List<ArticleResponse> = mutableListOf(),
    var petIdList: List<String> = mutableListOf()
): Parcelable