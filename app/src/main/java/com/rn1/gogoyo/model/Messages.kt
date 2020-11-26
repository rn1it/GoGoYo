package com.rn1.gogoyo.model

data class Messages(
    val sender: Users,
    val receiver: Users,
    val isRead: Boolean,
    val msgTime: Long = System.currentTimeMillis()
)