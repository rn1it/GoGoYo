package com.rn1.gogoyo.model

data class Points(
    val id: String,
    val latitude : Double,
    val longitude: Double,
    val createdTime: Long = System.currentTimeMillis()
)