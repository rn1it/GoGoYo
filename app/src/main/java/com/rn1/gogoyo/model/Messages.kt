package com.rn1.gogoyo.model

import java.util.*

data class Messages(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String? = "",
    val id: String = "",
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isRead: Boolean = false,
    val msgTime: Long = Calendar.getInstance().timeInMillis,
    var friendImg: String? = null
)