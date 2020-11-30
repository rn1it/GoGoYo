package com.rn1.gogoyo.model

import java.io.File

data class Articles(
    val id: String,
    val title: String,
    val authorId: String? = null,
    val createdTime: Long? = null,
    val images: List<File>? = null,
    val like: Int? = null
)