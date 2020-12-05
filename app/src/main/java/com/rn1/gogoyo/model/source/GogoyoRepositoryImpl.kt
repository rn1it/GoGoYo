package com.rn1.gogoyo.model.source

import com.rn1.gogoyo.model.Result


class GogoyoRepositoryImpl(
    private val remoteDataSource: GogoyoDataSource,
    private val localDataSource: GogoyoDataSource
): GogoyoRepository{

    override suspend fun login(id: String, name: String): Result<Boolean> {
        return remoteDataSource.login(id, name)
    }


}