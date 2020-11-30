package com.rn1.gogoyo.model

data class Chatroom(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val msgTime: Long? = null,
    val lastMsg: String? = null
)