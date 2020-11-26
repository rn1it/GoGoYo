package com.rn1.gogoyo.model

import java.io.File

data class Events(
    val id: String,
    val title: String,
    val host: Users,
    val participant: List<Users>,
    val images: List<File>,
    // 0: 揪團中, 1: 已成團, 2:進行中, 3: 已結束
    val status: Int,
    val walkId: String,
    val createdTime: Long = System.currentTimeMillis()
)