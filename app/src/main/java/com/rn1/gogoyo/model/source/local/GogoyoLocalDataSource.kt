package com.rn1.gogoyo.model.source.local

import android.content.Context
import com.rn1.gogoyo.model.Result
import com.rn1.gogoyo.model.source.GogoyoDataSource

class GogoyoLocalDataSource(val context: Context) : GogoyoDataSource {
    override suspend fun login(id: String, name: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

}