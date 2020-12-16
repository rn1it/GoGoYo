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
    var author: Users? = null,
    var createdTime: Long = -1,
    var images: List<String> = mutableListOf(),
    val like: Int = 0,
    val favoriteUserIdList: List<String> = mutableListOf(),
    val responseList: List<ArticleResponse> = mutableListOf(),
    var petIdList: List<String> = mutableListOf()
): Parcelable