package com.rn1.gogoyo.model

import java.io.File

data class Walk(
    val id: String,
    val user: Users,
    val pets: List<Pets>,
    val newFriend: List<Friends>,
    val points : List<Points>,
    val score: Int,
    val distance: Float,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long,
    val images: List<File>,
    val createdTime: Long = System.currentTimeMillis()
)