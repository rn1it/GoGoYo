package com.rn1.gogoyo.model

import java.util.*

data class Messages(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String? = "",
    val id: String = "",
    val isRead: Boolean = false,
    val msgTime: Long = Calendar.getInstance().timeInMillis
)