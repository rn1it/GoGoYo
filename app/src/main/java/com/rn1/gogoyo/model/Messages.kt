package com.rn1.gogoyo.model

data class Messages(
    val id: String,
    val sender: Users,
    val receiver: Users,
    val content: String? = "",
    val isRead: Boolean = false,
    val msgTime: Long = System.currentTimeMillis()
)