package com.rn1.gogoyo.model

import java.io.File

data class Articles(
    val id: String,
    val title: String,
    val authorId: String,
    val createdTime: Long,
    val images: List<File>,
    val like: Int
)