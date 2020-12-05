package com.rn1.gogoyo.model.source

import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result


class GogoyoRepositoryImpl(
    private val remoteDataSource: GogoyoDataSource,
    private val localDataSource: GogoyoDataSource
): GogoyoRepository{

    override suspend fun login(id: String, name: String): Result<Boolean> {
        return remoteDataSource.login(id, name)
    }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> {
        return remoteDataSource.newPets(pet, userId)
    }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> {
        return remoteDataSource.getAllPetsByUserId(userId)
    }

}