package com.rn1.gogoyo.model

data class Chatroom(
    val id: String,
    val userList: List<String>? = null,
    val msgTime: Long? = null,
    val lastMsg: String? = null
)