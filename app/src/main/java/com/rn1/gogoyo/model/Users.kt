package com.rn1.gogoyo.model

data class Users(
    val id: String,
    val name: String,
    val location: String? = null,
    val qrCode: String? = null,
    val currentLat: Double? = null,
    val currentLon: Double,
    val isWalking: Boolean = false
)