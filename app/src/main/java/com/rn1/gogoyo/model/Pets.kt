package com.rn1.gogoyo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Pets(
    var id: String = "",
    var name: String = "",
    // 品種
    var breed : String? = null,
    var sex: String? = null,
    var birth: String? = null,
    var interest: String? = null,
    var introduction: String? = null,
    var age: Int? = null,
    var weight: Float? = null,
    var video: String? = null,
    var image: String? = null,
    var voice: String? = null
): Parcelable