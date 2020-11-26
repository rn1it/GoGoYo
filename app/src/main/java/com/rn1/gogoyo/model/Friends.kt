package com.rn1.gogoyo.model

data class Friends(
    val friendId: String,
    //(0:沒關係, 1: 邀請中, 2: 好友關係)
    val status: Int
)