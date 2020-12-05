package com.rn1.gogoyo.model.source

import com.rn1.gogoyo.model.Pets
import com.rn1.gogoyo.model.Result

interface GogoyoDataSource {

    suspend fun login(id: String, name: String): Result<Boolean>

    suspend fun newPets(pet: Pets, userId: String): Result<Boolean>

    suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>>
}