package com.rn1.gogoyo.model.source

import androidx.lifecycle.MutableLiveData
import com.rn1.gogoyo.model.*


class GogoyoRepositoryImpl(
    private val remoteDataSource: GogoyoDataSource,
    private val localDataSource: GogoyoDataSource
): GogoyoRepository{

    override suspend fun login(id: String, name: String): Result<Boolean> {
        return remoteDataSource.login(id, name)
    }

    override suspend fun getUserById(id: String): Result<Users> {
        return remoteDataSource.getUserById(id)
    }

    override suspend fun newPets(pet: Pets, userId: String): Result<Boolean> {
        return remoteDataSource.newPets(pet, userId)
    }

    override suspend fun editPets(pet: Pets): Result<Boolean> {
        return remoteDataSource.editPets(pet)
    }

    override suspend fun getAllPetsByUserId(userId: String): Result<List<Pets>> {
        return remoteDataSource.getAllPetsByUserId(userId)
    }

    override suspend fun getPetsById(id: String): Result<Pets> {
        return remoteDataSource.getPetsById(id)
    }

    override suspend fun getPetsByIdList(idList: List<String>): Result<List<Pets>> {
        return remoteDataSource.getPetsByIdList(idList)
    }

    override suspend fun postArticle(article: Articles): Result<Boolean> {
        return remoteDataSource.postArticle(article)
    }

    override suspend fun getAllArticle(): Result<List<Articles>> {
        return remoteDataSource.getAllArticle()
    }

    override suspend fun getArticlesById(id: String): Result<List<Articles>> {
        return remoteDataSource.getArticlesById(id)
    }

    override suspend fun getFavoriteArticlesById(id: String): Result<List<Articles>> {
        return remoteDataSource.getFavoriteArticlesById(id)
    }

    override fun getRealTimeResponse(articleId: String): MutableLiveData<List<ArticleResponse>> {
        return remoteDataSource.getRealTimeResponse(articleId)
    }

    override fun getRealTimeArticle(articleId: String): MutableLiveData<Articles> {
        return remoteDataSource.getRealTimeArticle(articleId)
    }

    override suspend fun collectArticle(articleId: String, userId: String): Result<Boolean> {
        return remoteDataSource.collectArticle(articleId, userId)
    }

    override suspend fun responseArticle(articleId: String, response: ArticleResponse): Result<List<ArticleResponse>> {
        return remoteDataSource.responseArticle(articleId, response)
    }

}