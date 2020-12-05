package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Pets(
    val id: String,
    val name: String,
    // 品種
    val breed : String? = null,
    val sex: String? = null,
    val birth: String? = null,
    val interest: String? = null,
    val introduction: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val video: File? = null,
    val images: List<File>? = null,
    val voice: File? = null
): Parcelable