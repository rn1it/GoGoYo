package com.rn1.gogoyo.model.source

import com.rn1.gogoyo.model.Result

interface GogoyoDataSource {

    suspend fun login(id: String, name: String): Result<Boolean>



}