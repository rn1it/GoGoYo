package com.rn1.gogoyo.model

import java.io.File

data class Pets(
    val id: String,
    val name: String,
    // 品種
    val breed : String,
    val sex: String,
    val birth: String,
    val interest: String,
    val introduction: String,
    val age: Int,
    val weight: Float,
    val video: File,
    val images: List<File>,
    val voice: File
)